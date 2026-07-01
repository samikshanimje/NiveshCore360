package com.niveshcore360.repository;

import com.niveshcore360.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for Stock operations.
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByTicker(String ticker);
    boolean existsByTicker(String ticker);
}
