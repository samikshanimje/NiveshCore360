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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing holdings, stock listings, and mutual funds.
 */
@Service
@Transactional
public class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final PortfolioRepository portfolioRepository;
    private final StockRepository stockRepository;
    private final MutualFundRepository mutualFundRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogService auditLogService;

    @Autowired
    public InvestmentServiceImpl(InvestmentRepository investmentRepository,
                                 PortfolioRepository portfolioRepository,
                                 StockRepository stockRepository,
                                 MutualFundRepository mutualFundRepository,
                                 TransactionRepository transactionRepository,
                                 AuditLogService auditLogService) {
        this.investmentRepository = investmentRepository;
        this.portfolioRepository = portfolioRepository;
        this.stockRepository = stockRepository;
        this.mutualFundRepository = mutualFundRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public InvestmentDTO addInvestment(InvestmentDTO dto) {
        Portfolio portfolio = portfolioRepository.findById(dto.getPortfolioId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + dto.getPortfolioId()));

        Investment.InvestmentBuilder builder = Investment.builder()
                .portfolio(portfolio)
                .assetType(dto.getAssetType())
                .quantity(dto.getQuantity())
                .purchasePrice(dto.getPurchasePrice())
                .purchaseDate(dto.getPurchaseDate());

        String assetName = "";
        if (dto.getAssetType() == AssetType.STOCK) {
            Stock stock = stockRepository.findById(dto.getAssetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Stock not found with id: " + dto.getAssetId()));
            builder.stock(stock);
            assetName = stock.getTicker();
        } else {
            MutualFund mf = mutualFundRepository.findById(dto.getAssetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mutual Fund not found with id: " + dto.getAssetId()));
            builder.mutualFund(mf);
            assetName = mf.getFundName();
        }

        Investment investment = builder.build();
        Investment saved = investmentRepository.save(investment);

        // Record a transaction history entry
        BigDecimal totalAmount = dto.getQuantity().multiply(dto.getPurchasePrice());
        Transaction transaction = Transaction.builder()
                .portfolio(portfolio)
                .transactionType(TransactionType.BUY)
                .amount(totalAmount)
                .description("Buy " + dto.getQuantity() + " units of " + assetName)
                .build();
        transactionRepository.save(transaction);

        auditLogService.log("ADD_INVESTMENT", "Added holding of " + assetName + " (" + dto.getQuantity() + " units)");

        return AppMapper.toInvestmentDTO(saved);
    }

    @Override
    public InvestmentDTO editInvestment(Long investmentId, InvestmentDTO dto) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Investment holding not found with id: " + investmentId));

        BigDecimal originalCost = investment.getQuantity().multiply(investment.getPurchasePrice());
        BigDecimal newCost = dto.getQuantity().multiply(dto.getPurchasePrice());

        investment.setQuantity(dto.getQuantity());
        investment.setPurchasePrice(dto.getPurchasePrice());
        investment.setPurchaseDate(dto.getPurchaseDate());

        Investment updated = investmentRepository.save(investment);

        String assetName = updated.getAssetType() == AssetType.STOCK ? updated.getStock().getTicker() : updated.getMutualFund().getFundName();
        
        // Log transaction adjustment
        Transaction transaction = Transaction.builder()
                .portfolio(updated.getPortfolio())
                .transactionType(TransactionType.BUY)
                .amount(newCost.subtract(originalCost))
                .description("Adjusted holding " + assetName + " to " + dto.getQuantity() + " units")
                .build();
        transactionRepository.save(transaction);

        auditLogService.log("EDIT_INVESTMENT", "Modified holding " + assetName + " to " + dto.getQuantity() + " units.");

        return AppMapper.toInvestmentDTO(updated);
    }

    @Override
    public void deleteInvestment(Long investmentId) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Investment holding not found with id: " + investmentId));

        String assetName = investment.getAssetType() == AssetType.STOCK ? investment.getStock().getTicker() : investment.getMutualFund().getFundName();
        BigDecimal totalRefund = investment.getQuantity().multiply(investment.getPurchasePrice());

        // Create sell transaction trace
        Transaction transaction = Transaction.builder()
                .portfolio(investment.getPortfolio())
                .transactionType(TransactionType.SELL)
                .amount(totalRefund)
                .description("Liquidated holding of " + assetName + " (" + investment.getQuantity() + " units)")
                .build();
        transactionRepository.save(transaction);

        investmentRepository.delete(investment);

        auditLogService.log("DELETE_INVESTMENT", "Liquidated holding of " + assetName + ".");
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
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MutualFund> getAllMutualFunds() {
        return mutualFundRepository.findAll();
    }

    @Override
    public Stock createStock(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override
    public MutualFund createMutualFund(MutualFund mf) {
        return mutualFundRepository.save(mf);
    }
}
