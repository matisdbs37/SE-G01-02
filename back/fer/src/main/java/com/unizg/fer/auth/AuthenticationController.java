package com.unizg.fer.auth;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for authentication endpoints
 * Handles user registration and login
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Register a new user
     * POST /api/auth/register
     *
     * Request body example:
     * {
     * "email": "user@example.com",
     * "password": "securePassword123",
     * "firstName": "John",
     * "lastName": "Doe"
     * }
     *
     * @param request Registration request
     * @return AuthResponse with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        LOGGER.info("POST /api/auth/register - Registering user: {}", request.getEmail());

        AuthResponse response = authenticationService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login user
     * POST /api/auth/login
     *
     * Request body example:
     * {
     * "email": "user@example.com",
     * "password": "securePassword123"
     * }
     *
     * @param request Login request
     * @return AuthResponse with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        LOGGER.info("POST /api/auth/login - Login attempt for: {}", request.getEmail());

        AuthResponse response = authenticationService.login(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Test endpoint to verify token
     * GET /api/auth/validate
     * Header: Authorization: Bearer <token>
     *
     * @param authHeader Authorization header with Bearer token
     * @return Success message if token is valid
     */
    @GetMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        LOGGER.info("GET /api/auth/validate - Validating token");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid token format"));
        }

        String token = authHeader.substring(7);
        authenticationService.validateToken(token);

        return ResponseEntity.ok(new AuthResponse("Token is valid"));
    }

    @GetMapping("/status")
    public ResponseEntity<?> getAuthStatus(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> response = new HashMap<>();
        if (principal != null) {
            response.put("authenticated", true);
            response.put("name", principal.getAttribute("name"));
            response.put("email", principal.getAttribute("email"));
        } else {
            response.put("authenticated", false);
        }
        return ResponseEntity.ok(response);
    }
}