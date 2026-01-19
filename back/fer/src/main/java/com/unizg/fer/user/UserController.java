package com.unizg.fer.user;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.unizg.fer.stats.StatUpdater;
import com.unizg.fer.stats.StatsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for managing user-related operations.
 * Provides endpoints for retrieving, updating, deleting, and creating users.
 */
@RestController
@RequestMapping("/api/v2/")
@Tag(name = "User Management", description = "APIs for managing user operations including CRUD operations and authentication")
@SecurityRequirement(name = "bearer-jwt")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    public StatsService statsService;

    /**
     * Retrieves the user details based on the email extracted from the JWT token.
     *
     * @param jwt the JWT token containing the user's email claim
     * @return a `ResponseEntity` containing the user details
     */
    @Operation(summary = "Get current user details", description = "Retrieves the authenticated user's details using the email from the JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("user")
    public ResponseEntity<User> getUsersByEmail(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaim("email");
        var user = service.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Updates the user details based on the email extracted from the JWT token.
     *
     * @param jwt         the JWT token containing the user's email claim
     * @param updatedUser the updated user details
     * @return a `ResponseEntity` containing the updated user details
     */
    @Operation(summary = "Update current user", description = "Updates the authenticated user's details (name, city, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user data provided", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PostMapping("user/update")
    public ResponseEntity<User> updateUser(@AuthenticationPrincipal Jwt jwt, @RequestBody User updatedUser) {
        String email = jwt.getClaim("email");
        var user = service.updateUser(email, updatedUser);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get user by ID and log activity", description = "Retrieves user information by ID and updates user login statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found and stats updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })

    @GetMapping("user/{id}")
    public ResponseEntity<User> logUser(@PathVariable String id) {
        // first update stats
        LocalDateTime now = LocalDateTime.now();
        statsService.updateStats(id, StatUpdater.login(statsService, id, now));
        // second get user
        var user = service.getInfoById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Deletes the user based on the email extracted from the JWT token.
     *
     * @param jwt the JWT token containing the user's email claim
     * @return a `ResponseEntity` with a success message
     */
    @Operation(summary = "Delete current user", description = "Permanently deletes the authenticated user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "User deleted successfully"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })

    @DeleteMapping("user/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaim("email");
        service.deleteUser(email);
        return ResponseEntity.ok("User deleted successfully");
    }

    /**
     * Creates a new user based on the details extracted from the JWT token.
     *
     * @param jwt the JWT token containing the user's details (email, first name,
     *            last name, locale)
     * @return a `ResponseEntity` containing the created user details
     */
    @Operation(summary = "Create new user", description = "Creates a new user account using information from the JWT token (email, name, locale)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token data or user already exists", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content)
    })
    @PutMapping("user/create")
    public ResponseEntity<User> createUser(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaim("email");
        String firstName = jwt.getClaim("given_name");
        String lastName = jwt.getClaim("family_name");
        String city = jwt.getClaim("locale");
        String roles = "USER";
        User user = service.createUser(email, firstName, lastName, roles, city);
        return ResponseEntity.ok(user);
    }

    /**
     * 
     * @return
     */
    @GetMapping(value = "admin/users", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Stream<User>> getAllUsers() {
        var stream = service.findAll();
        return ResponseEntity.ok(stream);
    }

    /**
     * Deletes a specific user by email. Restricted to administrators.
     *
     * @param email the email of the user to delete passed as a request parameter
     * @return a `ResponseEntity` with a success message
     */
    @Operation(summary = "Delete user by email (Admin)", description = "Permanently deletes a specific user account by their email. Requires ADMIN privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "User user@example.com deleted successfully"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - Requires ROLE_ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @DeleteMapping("admin/user/delete")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUserByEmail(@RequestParam String email) {
        service.deleteUser(email);
        return ResponseEntity.ok("User " + email + " deleted successfully");
    }


    /**
     * Updates a specific user's details based on email. Restricted to administrators.
     *
     * @param email       the email of the user to update (passed as query param)
     * @param updatedUser the new user details
     * @return the updated user
     */
    @Operation(summary = "Update user by email (Admin)", description = "Updates a specific user's profile data. Requires ADMIN privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - Requires ROLE_ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PutMapping("admin/user/update")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> updateUserByEmailAdmin(
            @RequestParam String email,
            @RequestBody User updatedUser) {

        var user = service.updateUser(email, updatedUser);
        return ResponseEntity.ok(user);
    }
}