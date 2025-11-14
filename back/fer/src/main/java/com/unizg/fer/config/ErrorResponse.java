package com.unizg.fer.config;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Standard error response structure for API exceptions
 */
@Setter
@Getter
public class ErrorResponse {

    // Getters and Setters
    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String error, String message, String path, LocalDateTime timestamp) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }

    /**
     * Constructor for simple error responses
     */
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

}