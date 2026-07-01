package com.niveshcore360.service.impl;

import com.niveshcore360.entity.OTP;
import com.niveshcore360.repository.OTPRepository;
import com.niveshcore360.service.EmailService;
import com.niveshcore360.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of OTP verification service.
 */
@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private final SecureRandom random = new SecureRandom();

    @Override
    public String generateOtp(String email) {
        // Generate a 6-digit numeric OTP code
        int codeInt = 100000 + random.nextInt(900000);
        String code = String.valueOf(codeInt);

        // Define expiry of 5 minutes from now
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        OTP otp = OTP.builder()
            .email(email)
            .code(code)
            .expiryTime(expiry)
            .isUsed(false)
            .build();

        otpRepository.save(otp);

        // Dispatch verification code via email thread
        emailService.sendOtpEmail(email, code);

        return code;
    }

    @Override
    public boolean validateOtp(String email, String code) {
        Optional<OTP> recordOpt = otpRepository.findFirstByEmailAndCodeAndIsUsedOrderByExpiryTimeDesc(email, code, false);
        if (recordOpt.isEmpty()) {
            return false;
        }

        OTP otp = recordOpt.get();
        if (otp.isExpired()) {
            return false;
        }

        // Mark OTP as used
        otp.setUsed(true);
        otpRepository.save(otp);
        return true;
    }
}
