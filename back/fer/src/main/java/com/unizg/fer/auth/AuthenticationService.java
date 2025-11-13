package com.unizg.fer.auth;

import com.unizg.fer.config.DuplicateResourceException;
import com.unizg.fer.config.ResourceNotFoundException;
import com.unizg.fer.user.User;
import com.unizg.fer.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for handling user authentication (register and login)
 */
@Service
public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenService jwtTokenService;

    /**
     * Register a new user with encrypted password
     * @param request Registration request with user details
     * @return AuthResponse with JWT token
     * @throws DuplicateResourceException if email already exists
     */
    public AuthResponse register(RegisterRequest request) {
        LOGGER.info("Attempting to register user with email: {}", request.getEmail());

        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            LOGGER.error("User with email {} already exists", request.getEmail());
            throw new DuplicateResourceException("User with email " + request.getEmail() + " already exists");
        }

        // Create new user
        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Encrypt password with BCrypt
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(List.of("user")) // Default role
                .createdAt(LocalDate.now())
                .build();

        // Save to database
        User savedUser = userRepository.save(newUser);
        LOGGER.info("User registered successfully with id: {}", savedUser.getId());

        // Generate JWT token
        String token = jwtTokenService.generateToken(savedUser.getId(), savedUser.getEmail());

        return new AuthResponse(
                token,
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName()
        );
    }

    /**
     * Login user with email and password
     * @param request Login request with credentials
     * @return AuthResponse with JWT token
     * @throws ResourceNotFoundException if user not found
     * @throws IllegalArgumentException if password is incorrect
     */
    public AuthResponse login(LoginRequest request) {
        LOGGER.info("Attempting login for user: {}", request.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    LOGGER.error("User not found with email: {}", request.getEmail());
                    return new ResourceNotFoundException("Invalid email or password");
                });

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            LOGGER.error("Invalid password for user: {}", request.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }

        LOGGER.info("User logged in successfully: {}", request.getEmail());

        // Generate JWT token
        String token = jwtTokenService.generateToken(user.getId(), user.getEmail());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    /**
     * Validate JWT token and return user
     * @param token JWT token
     * @return User if token is valid
     * @throws ResourceNotFoundException if user not found
     */
    public User validateToken(String token) {
        String userId = jwtTokenService.extractUserId(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}