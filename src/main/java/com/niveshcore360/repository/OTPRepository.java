package com.niveshcore360.repository;

import com.niveshcore360.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for OTP codes verification.
 */
@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findFirstByEmailAndCodeAndIsUsedOrderByExpiryTimeDesc(String email, String code, boolean isUsed);
}
