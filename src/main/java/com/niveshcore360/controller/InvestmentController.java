package com.niveshcore360.controller;

import com.niveshcore360.dto.InvestmentDTO;
import com.niveshcore360.entity.AssetType;
import com.niveshcore360.entity.MutualFund;
import com.niveshcore360.entity.Stock;
import com.niveshcore360.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller class coordinating Investment holdings operations.
 */
@Controller
public class InvestmentController {

    private final InvestmentService investmentService;

    @Autowired
    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    public InvestmentDTO addInvestment(Long portfolioId, AssetType type, Long assetId, BigDecimal quantity, BigDecimal price, LocalDate date) {
        InvestmentDTO dto = InvestmentDTO.builder()
                .portfolioId(portfolioId)
                .assetType(type)
                .assetId(assetId)
                .quantity(quantity)
                .purchasePrice(price)
                .purchaseDate(date)
                .build();
        return investmentService.addInvestment(dto);
    }

    public InvestmentDTO editInvestment(Long investmentId, BigDecimal quantity, BigDecimal price, LocalDate date) {
        InvestmentDTO dto = InvestmentDTO.builder()
                .quantity(quantity)
                .purchasePrice(price)
                .purchaseDate(date)
                .build();
        return investmentService.editInvestment(investmentId, dto);
    }

    public void deleteInvestment(Long investmentId) {
        investmentService.deleteInvestment(investmentId);
    }

    public List<InvestmentDTO> getInvestments(Long portfolioId) {
        return investmentService.getInvestmentsByPortfolioId(portfolioId);
    }

    public List<InvestmentDTO> search(Long portfolioId, String keyword) {
        return investmentService.searchInvestments(portfolioId, keyword);
    }

    public List<InvestmentDTO> filter(Long portfolioId, AssetType type) {
        return investmentService.filterInvestments(portfolioId, type);
    }

    public List<Stock> getAvailableStocks() {
        return investmentService.getAllStocks();
    }

    public List<MutualFund> getAvailableMutualFunds() {
        return investmentService.getAllMutualFunds();
    }
}
