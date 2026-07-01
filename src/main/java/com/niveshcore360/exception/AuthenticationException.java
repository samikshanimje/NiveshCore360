package com.niveshcore360.exception;

/**
 * Exception thrown when authentication or registration validation fails.
 */
public class AuthenticationException extends NiveshCoreException {
    public AuthenticationException(String message) {
        super(message);
    }
}
