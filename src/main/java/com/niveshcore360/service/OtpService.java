package com.niveshcore360.service;

/**
 * Service interface for handling OTP security validations.
 */
public interface OtpService {
    String generateOtp(String email);
    boolean validateOtp(String email, String code);
}
