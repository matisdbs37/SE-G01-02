package com.unizg.fer.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Martin NERON
 * Uses of @ControllerAdvice to handle exceptions globally
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles NotAuthticatedException globally
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles NotAuthticatedException globally
     */
    @ExceptionHandler(NotAuthticatedException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthenticatedException(NotAuthticatedException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder(ex, HttpStatus.UNAUTHORIZED, ex.getMessage()).build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    //add other exception handlers as needed
}
