package com.niveshcore360.controller;

import com.niveshcore360.dto.PortfolioSummaryDTO;
import com.niveshcore360.entity.Portfolio;
import com.niveshcore360.exception.NiveshCoreException;
import com.niveshcore360.security.UserSession;
import com.niveshcore360.service.PortfolioService;
import com.niveshcore360.entity.Transaction;
import com.niveshcore360.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.util.Collections;
import java.util.List;

/**
 * Controller class managing Portfolio values and statistics.
 */
@Controller
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final UserSession userSession;
    private final TransactionRepository transactionRepository;

    @Autowired
    public PortfolioController(PortfolioService portfolioService, UserSession userSession, TransactionRepository transactionRepository) {
        this.portfolioService = portfolioService;
        this.userSession = userSession;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Gets all portfolios belonging to the currently logged-in user.
     */
    public List<Portfolio> getPortfolios() {
        if (!userSession.isLoggedIn()) {
            return Collections.emptyList();
        }
        return portfolioService.getPortfoliosByUserId(userSession.getCurrentUser().getId());
    }

    /**
     * Creates a new portfolio for the logged-in user.
     */
    public Portfolio createPortfolio(String name) {
        if (!userSession.isLoggedIn()) {
            throw new NiveshCoreException("Cannot create portfolio: No active session.");
        }
        return portfolioService.createPortfolio(userSession.getCurrentUser().getId(), name);
    }

    /**
     * Computes the aggregated summary valuation for the selected portfolio.
     */
    public PortfolioSummaryDTO getPortfolioSummary(Long portfolioId) {
        return portfolioService.getPortfolioSummary(portfolioId);
    }

    /**
     * Fetches transaction activity lists for the selected portfolio.
     */
    public List<Transaction> getTransactions(Long portfolioId) {
        return transactionRepository.findByPortfolioIdOrderByTransactionDateDesc(portfolioId);
    }
}
