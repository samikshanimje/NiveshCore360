package com.niveshcore360.service;

import com.niveshcore360.dto.PortfolioSummaryDTO;
import com.niveshcore360.entity.Portfolio;
import java.util.List;

/**
 * Service interface for Portfolio management and value metrics calculation.
 */
public interface PortfolioService {
    Portfolio createPortfolio(Long userId, String name);
    List<Portfolio> getPortfoliosByUserId(Long userId);
    Portfolio getPortfolioById(Long id);
    PortfolioSummaryDTO getPortfolioSummary(Long portfolioId);
    void deletePortfolio(Long id);
}
