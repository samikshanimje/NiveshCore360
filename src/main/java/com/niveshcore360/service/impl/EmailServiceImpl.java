package com.niveshcore360.service.impl;

import com.niveshcore360.entity.EmailLog;
import com.niveshcore360.repository.EmailLogRepository;
import com.niveshcore360.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.File;

/**
 * Service implementation for sending emails, supporting attachment reports and saving logs to DB.
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private EmailLogRepository emailLogRepository;

    @Override
    public void sendEmail(String to, String subject, String body) {
        String status = "SENT";
        try {
            if (mailSender != null) {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(body, true);
                mailSender.send(message);
            } else {
                log.info("[SIMULATED EMAIL] To: {}, Subject: {}, Body: {}", to, subject, body);
            }
        } catch (Exception e) {
            log.error("Failed to send email to " + to, e);
            status = "FAILED";
        } finally {
            saveEmailLog(to, subject, status);
        }
    }

    @Override
    public void sendOtpEmail(String to, String otpCode) {
        String subject = "NiveshCore360 - OTP Verification Code";
        String body = String.format(
            "<div style='font-family: Arial, sans-serif; background-color: #0f172a; color: #f8fafc; padding: 20px; border-radius: 8px; max-width: 500px;'>" +
            "<h2 style='color: #8b5cf6; margin-bottom: 5px;'>NiveshCore360 Security</h2>" +
            "<p style='color: #94a3b8; font-size: 14px;'>Invest. Track. Grow. Securely.</p>" +
            "<hr style='border-color: #334155; margin: 20px 0;'/>" +
            "<p>Your security verification one-time password code is:</p>" +
            "<div style='background-color: #1e293b; padding: 15px; border-radius: 6px; text-align: center; margin: 20px 0;'>" +
            "<span style='font-size: 28px; font-weight: bold; color: #10b981; letter-spacing: 4px;'>%s</span>" +
            "</div>" +
            "<p style='color: #94a3b8; font-size: 12px;'>This code is active for 5 minutes. If you did not request this, please secure your profile credentials immediately.</p>" +
            "</div>",
            otpCode
        );
        sendEmail(to, subject, body);
    }

    @Override
    public void sendStatementEmail(String to, String portfolioName, String attachmentPath) {
        String subject = "NiveshCore360 - Portfolio Statement: " + portfolioName;
        String body = "<div style='font-family: Arial, sans-serif; background-color: #0f172a; color: #f8fafc; padding: 20px; border-radius: 8px; max-width: 500px;'>" +
            "<h2 style='color: #8b5cf6;'>Statement Export Successful</h2>" +
            "<p>Please find attached your compiled statement report details for portfolio <b>" + portfolioName + "</b>.</p>" +
            "</div>";

        String status = "SENT";
        try {
            if (mailSender != null) {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(body, true);

                File file = new File(attachmentPath);
                if (file.exists()) {
                    FileSystemResource res = new FileSystemResource(file);
                    helper.addAttachment(file.getName(), res);
                }
                mailSender.send(message);
            } else {
                log.info("[SIMULATED EMAIL WITH ATTACHMENT] To: {}, Subject: {}, File: {}", to, subject, attachmentPath);
            }
        } catch (Exception e) {
            log.error("Failed to send statement email to " + to, e);
            status = "FAILED";
        } finally {
            saveEmailLog(to, subject, status);
        }
    }

    private void saveEmailLog(String to, String subject, String status) {
        try {
            EmailLog logEntry = EmailLog.builder()
                .recipient(to)
                .subject(subject)
                .status(status)
                .build();
            emailLogRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Failed to save email log to database", e);
        }
    }
}
