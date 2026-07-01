package com.niveshcore360.repository;

import com.niveshcore360.entity.Portfolio;
import com.niveshcore360.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for Portfolio operations.
 */
@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByUser(User user);
    List<Portfolio> findByUserId(Long userId);
}
