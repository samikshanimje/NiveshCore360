package com.niveshcore360.repository;

import com.niveshcore360.entity.Goal;
import com.niveshcore360.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for financial goal operations.
 */
@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByPortfolio(Portfolio portfolio);
    List<Goal> findByPortfolioId(Long portfolioId);
}
