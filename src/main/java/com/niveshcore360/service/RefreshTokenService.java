package com.niveshcore360.service;

import com.niveshcore360.entity.RefreshToken;
import java.util.Optional;

/**
 * Service interface for handling RefreshToken rotation.
 */
public interface RefreshTokenService {
    RefreshToken createRefreshToken(Long userId);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByUserId(Long userId);
}
