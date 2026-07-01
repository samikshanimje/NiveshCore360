package com.niveshcore360.exception;

/**
 * Base Runtime exception class for NiveshCore360.
 */
public class NiveshCoreException extends RuntimeException {
    public NiveshCoreException(String message) {
        super(message);
    }
    public NiveshCoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
