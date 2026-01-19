package com.unizg.fer.config;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

/**
 * Global exception handler for all REST controllers
 * Catches exceptions and returns formatted error responses
 *
 * @author Martin NERON
 *         Uses @RestControllerAdvice to handle exceptions globally
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<Map<String, Object>> handleContentNotFound(ResourceNotFoundException ex) {
                LOGGER.warn("Content not found: {}", ex.getMessage());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("timestamp", LocalDateTime.now());
                errorResponse.put("message", ex.getMessage());
                errorResponse.put("status", HttpStatus.NOT_FOUND.value());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
                LOGGER.warn("Invalid argument: {}", ex.getMessage());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("timestamp", LocalDateTime.now());
                errorResponse.put("message", ex.getMessage());
                errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<Map<String, Object>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("timestamp", LocalDateTime.now());
                errorResponse.put("status", HttpStatus.CONFLICT.value());
                errorResponse.put("error", "Conflict");
                errorResponse.put("message", ex.getMessage());

                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
                LOGGER.error("Unexpected error occurred: {}", ex.getMessage(), ex);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("timestamp", LocalDateTime.now());
                errorResponse.put("message", "An unexpected error occurred");
                errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

        @ExceptionHandler(AccessDeniedException.class) // Capture AuthorizationDeniedException et ses parents
        public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
                LOGGER.warn("Access denied: {}", ex.getMessage()); // .warn est souvent mieux que .error pour ça

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("timestamp", LocalDateTime.now());
                errorResponse.put("message", "Access denied: You don't have the right permission.");
                errorResponse.put("status", HttpStatus.FORBIDDEN.value()); // 403

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
}
