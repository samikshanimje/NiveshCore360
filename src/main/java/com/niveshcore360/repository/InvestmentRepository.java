package com.niveshcore360.repository;

import com.niveshcore360.entity.Investment;
import com.niveshcore360.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for Investment holdings.
 */
@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findByPortfolio(Portfolio portfolio);
    List<Investment> findByPortfolioId(Long portfolioId);
}
