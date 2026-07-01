package com.niveshcore360.exception;

/**
 * Exception thrown when a requested database entity is not found.
 */
public class ResourceNotFoundException extends NiveshCoreException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
