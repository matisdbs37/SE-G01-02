package com.unizg.fer.user;

import com.unizg.fer.config.ResourceNotFoundException;
import com.unizg.fer.config.DuplicateResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        LOGGER.info("Fetching all users");
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     * @param id User ID
     * @return User entity
     * @throws ResourceNotFoundException if user not found
     */
    public User getUserById(String id) {
        LOGGER.info("Fetching user with id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * Get user by email
     * @param email User email
     * @return User entity
     * @throws ResourceNotFoundException if user not found
     */
    public User getUserByEmail(String email) {
        LOGGER.info("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Create a new user
     * @param user User entity to create
     * @return Created user
     * @throws DuplicateResourceException if email already exists
     */
    public User createUser(User user) {
        LOGGER.info("Creating new user with email: {}", user.getEmail());

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User with email " + user.getEmail() + " already exists");
        }

        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDate.now());
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(List.of("user"));
        }

        User savedUser = userRepository.save(user);
        LOGGER.info("User created successfully with id: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Update an existing user
     * @param id User ID
     * @param userDetails Updated user details
     * @return Updated user
     * @throws ResourceNotFoundException if user not found
     */
    public User updateUser(String id, User userDetails) {
        LOGGER.info("Updating user with id: {}", id);

        User user = getUserById(id);

        if (userDetails.getFirstName() != null) {
            user.setFirstName(userDetails.getFirstName());
        }

        if (userDetails.getLastName() != null) {
            user.setLastName(userDetails.getLastName());
        }

        if (userDetails.getRoles() != null && !userDetails.getRoles().isEmpty()) {
            user.setRoles(userDetails.getRoles());
        }

        User updatedUser = userRepository.save(user);
        LOGGER.info("User updated successfully with id: {}", updatedUser.getId());
        return updatedUser;
    }

    /**
     * Delete a user
     * @param id User ID
     * @throws ResourceNotFoundException if user not found
     */
    public void deleteUser(String id) {
        LOGGER.info("Deleting user with id: {}", id);

        User user = getUserById(id);
        userRepository.delete(user);

        LOGGER.info("User deleted successfully with id: {}", id);
    }

    /**
     * Update user roles
     * @param id User ID
     * @param roles New roles list
     * @return Updated user
     * @throws ResourceNotFoundException if user not found
     */
    public User updateUserRoles(String id, List<String> roles) {
        LOGGER.info("Updating roles for user with id: {}", id);

        User user = getUserById(id);
        user.setRoles(roles);

        User updatedUser = userRepository.save(user);
        LOGGER.info("User roles updated successfully for id: {}", updatedUser.getId());
        return updatedUser;
    }

    /**
     * Check if user exists by email
     * @param email User email
     * @return true if exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}