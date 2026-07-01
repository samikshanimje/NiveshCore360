package com.niveshcore360.service;

import com.niveshcore360.dto.InvestmentDTO;
import com.niveshcore360.entity.AssetType;
import com.niveshcore360.entity.MutualFund;
import com.niveshcore360.entity.Stock;
import java.util.List;

/**
 * Service interface for Investment operations (Add, Edit, Delete, Search, Filter).
 */
public interface InvestmentService {
    InvestmentDTO addInvestment(InvestmentDTO dto);
    InvestmentDTO editInvestment(Long investmentId, InvestmentDTO dto);
    void deleteInvestment(Long investmentId);
    List<InvestmentDTO> getInvestmentsByPortfolioId(Long portfolioId);
    List<InvestmentDTO> searchInvestments(Long portfolioId, String keyword);
    List<InvestmentDTO> filterInvestments(Long portfolioId, AssetType assetType);

    // Metadata management for Stocks and Mutual Funds
    List<Stock> getAllStocks();
    List<MutualFund> getAllMutualFunds();
    Stock createStock(Stock stock);
    MutualFund createMutualFund(MutualFund mf);
}
