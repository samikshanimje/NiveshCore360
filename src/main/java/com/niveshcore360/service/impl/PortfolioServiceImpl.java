package com.niveshcore360.service.impl;

import com.niveshcore360.dto.InvestmentDTO;
import com.niveshcore360.dto.PortfolioSummaryDTO;
import com.niveshcore360.entity.AssetType;
import com.niveshcore360.entity.Portfolio;
import com.niveshcore360.entity.User;
import com.niveshcore360.exception.ResourceNotFoundException;
import com.niveshcore360.mapper.AppMapper;
import com.niveshcore360.repository.InvestmentRepository;
import com.niveshcore360.repository.PortfolioRepository;
import com.niveshcore360.repository.UserRepository;
import com.niveshcore360.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation managing user portfolios and calculating value calculations.
 */
@Service
@Transactional
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final InvestmentRepository investmentRepository;

    @Autowired
    public PortfolioServiceImpl(PortfolioRepository portfolioRepository,
                                UserRepository userRepository,
                                InvestmentRepository investmentRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.investmentRepository = investmentRepository;
    }

    @Override
    public Portfolio createPortfolio(Long userId, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Portfolio portfolio = Portfolio.builder()
                .name(name)
                .user(user)
                .build();
        return portfolioRepository.save(portfolio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Portfolio> getPortfoliosByUserId(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Portfolio getPortfolioById(Long id) {
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioSummaryDTO getPortfolioSummary(Long portfolioId) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        List<InvestmentDTO> investments = investmentRepository.findByPortfolioId(portfolioId)
                .stream()
                .map(AppMapper::toInvestmentDTO)
                .collect(Collectors.toList());

        BigDecimal totalCostBasis = BigDecimal.ZERO;
        BigDecimal totalCurrentValue = BigDecimal.ZERO;
        BigDecimal stockValue = BigDecimal.ZERO;
        BigDecimal mutualFundValue = BigDecimal.ZERO;
        Map<String, BigDecimal> categoryAllocation = new HashMap<>();

        for (InvestmentDTO inv : investments) {
            totalCostBasis = totalCostBasis.add(inv.getCostBasis());
            totalCurrentValue = totalCurrentValue.add(inv.getCurrentValue());

            if (inv.getAssetType() == AssetType.STOCK) {
                stockValue = stockValue.add(inv.getCurrentValue());
            } else {
                mutualFundValue = mutualFundValue.add(inv.getCurrentValue());
            }

            categoryAllocation.merge(inv.getSymbol(), inv.getCurrentValue(), BigDecimal::add);
        }

        BigDecimal totalProfitLoss = totalCurrentValue.subtract(totalCostBasis);
        BigDecimal totalProfitLossPercentage = BigDecimal.ZERO;
        if (totalCostBasis.compareTo(BigDecimal.ZERO) > 0) {
            totalProfitLossPercentage = totalProfitLoss.multiply(BigDecimal.valueOf(100))
                    .divide(totalCostBasis, 2, RoundingMode.HALF_UP);
        }

        BigDecimal stockWeight = BigDecimal.ZERO;
        BigDecimal mutualFundWeight = BigDecimal.ZERO;

        if (totalCurrentValue.compareTo(BigDecimal.ZERO) > 0) {
            stockWeight = stockValue.multiply(BigDecimal.valueOf(100))
                    .divide(totalCurrentValue, 2, RoundingMode.HALF_UP);
            mutualFundWeight = mutualFundValue.multiply(BigDecimal.valueOf(100))
                    .divide(totalCurrentValue, 2, RoundingMode.HALF_UP);
        }

        return PortfolioSummaryDTO.builder()
                .portfolioId(portfolioId)
                .portfolioName(portfolio.getName())
                .totalInvestment(totalCostBasis)
                .totalCurrentValue(totalCurrentValue)
                .totalProfitLoss(totalProfitLoss)
                .totalProfitLossPercentage(totalProfitLossPercentage)
                .stockWeight(stockWeight)
                .mutualFundWeight(mutualFundWeight)
                .categoryAllocation(categoryAllocation)
                .build();
    }

    @Override
    public void deletePortfolio(Long id) {
        portfolioRepository.deleteById(id);
    }
}
