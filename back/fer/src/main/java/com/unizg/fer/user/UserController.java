package com.unizg.fer.user;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.unizg.fer.stats.StatUpdater;
import com.unizg.fer.stats.StatsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing user-related operations.
 * Provides endpoints for retrieving, updating, deleting, and creating users.
 */
@RestController
@RequestMapping("/api/v2/")
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
    @GetMapping("user")
    public ResponseEntity<User> getUsersByEmail(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaim("email");
        var user = service.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    /**
     * Updates the user details based on the email extracted from the JWT token.
     *
     * @param jwt the JWT token containing the user's email claim
     * @param updatedUser the updated user details
     * @return a `ResponseEntity` containing the updated user details
     */
    @PostMapping("user/update")
    public ResponseEntity<User> updateUser(@AuthenticationPrincipal Jwt jwt, @RequestBody User updatedUser) {
        String email = jwt.getClaim("email");
        var user = service.updateUser(email, updatedUser);
        return ResponseEntity.ok(user);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<User> logUser(@PathVariable String id) {
        //first update stats
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
    @DeleteMapping("user/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaim("email");
        service.deleteUser(email);
        return ResponseEntity.ok("User deleted successfully");
    }

    /**
     * Creates a new user based on the details extracted from the JWT token.
     *
     * @param jwt the JWT token containing the user's details (email, first name, last name, locale)
     * @return a `ResponseEntity` containing the created user details
     */
    @PutMapping("user/create")
    public ResponseEntity<User> createUser(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaim("email");
        String firstName = jwt.getClaim("given_name");
        String lastName = jwt.getClaim("family_name");
        String city = jwt.getClaim("locale");
        // TODO: assign roles based on token roles
        String roles = "USER";
        User user = service.createUser(email, firstName, lastName, roles, city);
        return ResponseEntity.ok(user);
    }
}