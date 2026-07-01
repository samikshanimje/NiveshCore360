package com.niveshcore360.repository;

import com.niveshcore360.entity.MutualFund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for Mutual Fund operations.
 */
@Repository
public interface MutualFundRepository extends JpaRepository<MutualFund, Long> {
    Optional<MutualFund> findByFundName(String fundName);
    boolean existsByFundName(String fundName);
}
