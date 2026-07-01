package com.niveshcore360.service.impl;

import com.niveshcore360.dto.InvestmentDTO;
import com.niveshcore360.entity.*;
import com.niveshcore360.exception.ResourceNotFoundException;
import com.niveshcore360.mapper.AppMapper;
import com.niveshcore360.repository.*;
import com.niveshcore360.service.AuditLogService;
import com.niveshcore360.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation managing user investment holdings and unified assets.
 */
@Service
@Transactional
public class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogService auditLogService;

    @Autowired
    public InvestmentServiceImpl(InvestmentRepository investmentRepository,
                                 PortfolioRepository portfolioRepository,
                                 AssetRepository assetRepository,
                                 TransactionRepository transactionRepository,
                                 AuditLogService auditLogService) {
        this.investmentRepository = investmentRepository;
        this.portfolioRepository = portfolioRepository;
        this.assetRepository = assetRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public InvestmentDTO addInvestment(InvestmentDTO dto) {
        Portfolio portfolio = portfolioRepository.findById(dto.getPortfolioId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + dto.getPortfolioId()));

        Asset asset = assetRepository.findById(dto.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + dto.getAssetId()));

        Investment investment = Investment.builder()
                .portfolio(portfolio)
                .assetType(dto.getAssetType())
                .asset(asset)
                .quantity(dto.getQuantity())
                .purchasePrice(dto.getPurchasePrice())
                .purchaseDate(dto.getPurchaseDate())
                .build();

        Investment saved = investmentRepository.save(investment);

        // Record transaction
        BigDecimal totalAmount = dto.getQuantity().multiply(dto.getPurchasePrice());
        Transaction transaction = Transaction.builder()
                .portfolio(portfolio)
                .transactionType(TransactionType.BUY)
                .amount(totalAmount)
                .description("Buy " + dto.getQuantity() + " units of " + asset.getSymbol())
                .build();
        transactionRepository.save(transaction);

        auditLogService.log("ADD_HOLDING", "Added " + asset.getSymbol() + " holding (" + dto.getQuantity() + " units)");

        return AppMapper.toInvestmentDTO(saved);
    }

    @Override
    public InvestmentDTO editInvestment(Long investmentId, InvestmentDTO dto) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found with id: " + investmentId));

        BigDecimal originalCost = investment.getQuantity().multiply(investment.getPurchasePrice());
        BigDecimal newCost = dto.getQuantity().multiply(dto.getPurchasePrice());

        investment.setQuantity(dto.getQuantity());
        investment.setPurchasePrice(dto.getPurchasePrice());
        investment.setPurchaseDate(dto.getPurchaseDate());

        Investment updated = investmentRepository.save(investment);
        String assetSymbol = updated.getAsset().getSymbol();

        // Adjust transaction amount difference
        Transaction transaction = Transaction.builder()
                .portfolio(updated.getPortfolio())
                .transactionType(TransactionType.BUY)
                .amount(newCost.subtract(originalCost))
                .description("Adjusted holding " + assetSymbol + " to " + dto.getQuantity() + " units")
                .build();
        transactionRepository.save(transaction);

        auditLogService.log("EDIT_HOLDING", "Adjusted holding " + assetSymbol + " to " + dto.getQuantity() + " units");

        return AppMapper.toInvestmentDTO(updated);
    }

    @Override
    public void deleteInvestment(Long investmentId) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found with id: " + investmentId));

        String assetSymbol = investment.getAsset().getSymbol();
        BigDecimal refund = investment.getQuantity().multiply(investment.getPurchasePrice());

        Transaction transaction = Transaction.builder()
                .portfolio(investment.getPortfolio())
                .transactionType(TransactionType.SELL)
                .amount(refund)
                .description("Liquidated holding of " + assetSymbol + " (" + investment.getQuantity() + " units)")
                .build();
        transactionRepository.save(transaction);

        investmentRepository.delete(investment);
        auditLogService.log("DELETE_HOLDING", "Liquidated holding of " + assetSymbol);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentDTO> getInvestmentsByPortfolioId(Long portfolioId) {
        return investmentRepository.findByPortfolioId(portfolioId)
                .stream()
                .map(AppMapper::toInvestmentDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentDTO> searchInvestments(Long portfolioId, String keyword) {
        String cleanKeyword = keyword.toLowerCase().trim();
        return getInvestmentsByPortfolioId(portfolioId).stream()
                .filter(inv -> inv.getSymbol().toLowerCase().contains(cleanKeyword)
                        || inv.getName().toLowerCase().contains(cleanKeyword))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentDTO> filterInvestments(Long portfolioId, AssetType assetType) {
        return getInvestmentsByPortfolioId(portfolioId).stream()
                .filter(inv -> inv.getAssetType() == assetType)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asset> getAllStocks() {
        return assetRepository.findByAssetType(AssetType.STOCK);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asset> getAllMutualFunds() {
        return assetRepository.findByAssetType(AssetType.MUTUAL_FUND);
    }

    @Override
    public Asset createAsset(Asset asset) {
        return assetRepository.save(asset);
    }
}
