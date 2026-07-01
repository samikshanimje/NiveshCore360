package com.niveshcore360.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Dependency-free, secure provider for generating, verifying, and parsing HS256 signed JSON Web Tokens (JWT).
 */
public class JwtTokenProvider {

    private static final String JWT_SECRET = "niveshcore360_secure_jwt_secret_key_2026_enterprise_production";
    private static final long JWT_EXPIRATION_MS = 86400000; // 24 Hours

    /**
     * Generates a signed JWT token containing subject and roles attributes.
     */
    public static String generateToken(String username, String role) {
        try {
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            long now = System.currentTimeMillis();
            String payload = String.format(
                "{\"sub\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d}",
                username, role, now / 1000, (now + JWT_EXPIRATION_MS) / 1000
            );

            String base64Header = base64UrlEncode(header.getBytes(StandardCharsets.UTF_8));
            String base64Payload = base64UrlEncode(payload.getBytes(StandardCharsets.UTF_8));
            String signatureInput = base64Header + "." + base64Payload;

            byte[] signature = hmacSha256(signatureInput, JWT_SECRET);
            String base64Signature = base64UrlEncode(signature);

            return signatureInput + "." + base64Signature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT security token", e);
        }
    }

    /**
     * Verifies the token signature and expiration bounds.
     */
    public static boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String base64Header = parts[0];
            String base64Payload = parts[1];
            String base64Signature = parts[2];

            String signatureInput = base64Header + "." + base64Payload;
            byte[] signature = hmacSha256(signatureInput, JWT_SECRET);
            String expectedSignature = base64UrlEncode(signature);

            if (!expectedSignature.equals(base64Signature)) return false;

            // Check Expiry
            String payload = new String(base64UrlDecode(base64Payload), StandardCharsets.UTF_8);
            long exp = getJsonFieldLong(payload, "exp");
            return (System.currentTimeMillis() / 1000) <= exp;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract subject username from claims payload.
     */
    public static String getUsernameFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(base64UrlDecode(parts[1]), StandardCharsets.UTF_8);
            return getJsonFieldString(payload, "sub");
        } catch (Exception e) {
            return null;
        }
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static byte[] base64UrlDecode(String base64) {
        return Base64.getUrlDecoder().decode(base64);
    }

    private static byte[] hmacSha256(String data, String key) throws Exception {
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256HMAC.init(secretKey);
        return sha256HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private static String getJsonFieldString(String json, String fieldName) {
        String search = "\"" + fieldName + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    private static long getJsonFieldLong(String json, String fieldName) {
        String search = "\"" + fieldName + "\":";
        int start = json.indexOf(search);
        if (start == -1) return 0;
        start += search.length();
        int end = json.indexOf(",");
        if (end == -1 || end < start) end = json.indexOf("}", start);
        return Long.parseLong(json.substring(start, end).trim());
    }
}
