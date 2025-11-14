package com.unizg.fer.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for User CRUD operations
 * Separate from UserController to keep OAuth2 logic isolated
 */
@RestController
@RequestMapping("/users")
public class UserCrudController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCrudController.class);

    @Autowired
    private UserService userService;

    /**
     * Get all users
     * Requires admin role
     * 
     * @return List of all users
     */
    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<User>> getAllUsers() {
        LOGGER.info("GET request for all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Get user by ID
     * 
     * @param id User ID
     * @return User entity
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        LOGGER.info("GET request for user with id: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Get user by email
     * Requires admin role
     * 
     * @param email User email
     * @return User entity
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        LOGGER.info("GET request for user with email: {}", email);
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    /**
     * Create a new user
     * Requires admin role
     * 
     * @param user User entity to create
     * @return Created user with HTTP 201 status
     */
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        LOGGER.info("POST request to create user with email: {}", user.getEmail());
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Update an existing user
     * 
     * @param email User email
     * @param user  Updated user details
     * @return Updated user
     */
    @PutMapping("/{email}")
    public ResponseEntity<User> updateUser(
            @PathVariable String email,
            @RequestBody User user) {
        LOGGER.info("PUT request to update user with id: {}", email);
        User updatedUser = userService.updateUser(email, user);
        LOGGER.info("User updated");
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Update user roles
     * Requires admin role
     * 
     * @param id    User ID
     * @param roles New roles list
     * @return Updated user
     */
    @PatchMapping("/{id}/roles")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<User> updateUserRoles(
            @PathVariable String id,
            @RequestBody List<String> roles) {
        LOGGER.info("PATCH request to update roles for user with id: {}", id);
        User updatedUser = userService.updateUserRoles(id, roles);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete a user
     * Requires admin role
     * 
     * @param id User ID
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        LOGGER.info("DELETE request for user with id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if user exists by email
     * 
     * @param email User email
     * @return true if exists, false otherwise
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkUserExists(@RequestParam String email) {
        LOGGER.info("GET request to check if user exists with email: {}", email);
        return ResponseEntity.ok(userService.existsByEmail(email));
    }
}