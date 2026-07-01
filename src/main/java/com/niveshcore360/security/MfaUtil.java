package com.niveshcore360.security;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Self-contained utility for Time-Based One-Time Passwords (TOTP) to support 2FA.
 */
public class MfaUtil {

    private static final String BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    /**
     * Generates a random 2FA secret key.
     */
    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return encodeBase32(bytes);
    }

    /**
     * Verifies the provided 6-digit TOTP code against the secret key.
     * Incorporates a time window variance of +/- 30 seconds for clock drift.
     */
    public static boolean verifyCode(String secret, int code) {
        long timeIndex = System.currentTimeMillis() / 1000 / 30;
        try {
            byte[] decodedKey = decodeBase32(secret);
            for (int i = -1; i <= 1; i++) {
                if (getTOTP(decodedKey, timeIndex + i) == code) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("MFA verification failure: " + e.getMessage());
        }
        return false;
    }

    /**
     * Compiles a Google Authenticator configuration URI.
     */
    public static String getQrCodeUrl(String email, String secret) {
        return "otpauth://totp/NiveshCore360:" + email + "?secret=" + secret + "&issuer=NiveshCore360";
    }

    private static String encodeBase32(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int val = 0;
        int bits = 0;
        for (byte b : bytes) {
            val = (val << 8) | (b & 0xff);
            bits += 8;
            while (bits >= 5) {
                bits -= 5;
                sb.append(BASE32_CHARS.charAt((val >> bits) & 0x1f));
            }
        }
        if (bits > 0) {
            sb.append(BASE32_CHARS.charAt((val << (5 - bits)) & 0x1f));
        }
        return sb.toString();
    }

    private static byte[] decodeBase32(String base32) {
        String clean = base32.toUpperCase().replaceAll("[^A-Z2-7]", "");
        int length = clean.length();
        byte[] out = new byte[length * 5 / 8];
        int buffer = 0;
        int bits = 0;
        int index = 0;
        for (int i = 0; i < length; i++) {
            int val = BASE32_CHARS.indexOf(clean.charAt(i));
            buffer = (buffer << 5) | val;
            bits += 5;
            if (bits >= 8) {
                bits -= 8;
                out[index++] = (byte) ((buffer >> bits) & 0xff);
            }
        }
        return out;
    }

    private static int getTOTP(byte[] key, long time) throws GeneralSecurityException {
        byte[] data = new byte[8];
        long value = time;
        for (int i = 7; i >= 0; i--) {
            data[i] = (byte) (value & 0xFF);
            value >>= 8;
        }

        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);

        int offset = hash[hash.length - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < 4; i++) {
            truncatedHash = (truncatedHash << 8) | (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;

        return (int) truncatedHash;
    }
}
