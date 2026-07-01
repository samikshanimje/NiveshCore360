package com.niveshcore360.service;

import com.niveshcore360.dto.InvestmentDTO;
import com.niveshcore360.entity.AssetType;
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

    // Metadata management for Unified Assets
    List<com.niveshcore360.entity.Asset> getAllStocks();
    List<com.niveshcore360.entity.Asset> getAllMutualFunds();
    com.niveshcore360.entity.Asset createAsset(com.niveshcore360.entity.Asset asset);
}
