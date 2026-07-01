package com.niveshcore360.service.impl;

import com.niveshcore360.entity.RefreshToken;
import com.niveshcore360.entity.User;
import com.niveshcore360.repository.RefreshTokenRepository;
import com.niveshcore360.repository.UserRepository;
import com.niveshcore360.service.RefreshTokenService;
import com.niveshcore360.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for refresh token session management.
 */
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final long REFRESH_EXPIRATION_MS = 604800000; // 7 Days

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Delete any existing token for this user
        refreshTokenRepository.deleteByUser(user);

        RefreshToken token = RefreshToken.builder()
            .user(user)
            .token(UUID.randomUUID().toString())
            .expiryDate(Instant.now().plusMillis(REFRESH_EXPIRATION_MS))
            .build();

        return refreshTokenRepository.save(token);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please sign in again.");
        }
        return token;
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        refreshTokenRepository.deleteByUser(user);
    }
}
