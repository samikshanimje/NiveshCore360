package com.niveshcore360.service;

/**
 * Service interface for outgoing notification emails.
 */
public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendOtpEmail(String to, String otpCode);
    void sendStatementEmail(String to, String portfolioName, String attachmentPath);
}
