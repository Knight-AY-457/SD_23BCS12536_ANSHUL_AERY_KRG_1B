package com.example.ratelimitdemo.config;

/**
 * Raised when API key is missing or unknown.
 */
public class InvalidApiKeyException extends RuntimeException {

    public InvalidApiKeyException(String message) {
        super(message);
    }
}
