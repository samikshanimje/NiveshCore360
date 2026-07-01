package com.niveshcore360.repository;

import com.niveshcore360.entity.Portfolio;
import com.niveshcore360.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for transaction records.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByPortfolio(Portfolio portfolio);
    List<Transaction> findByPortfolioId(Long portfolioId);
    List<Transaction> findByPortfolioIdOrderByTransactionDateDesc(Long portfolioId);
}
