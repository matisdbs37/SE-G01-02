package com.unizg.fer.user;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unizg.fer.stats.StatUpdater;
import com.unizg.fer.stats.StatsService;

@RestController
@RequestMapping("/api/v2/user")
public class UserController {

    // TODO get user by ID because email in url is NOT secure;

    // TODO make a function that return the user id from an access token

    @Autowired
    public UserService service;

    @Autowired
    public StatsService statsService;

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserInfo(@PathVariable String email) {
        // first update stats
        // TODO
        // second get user
        var user = service.getInfoByEmail(email);
        return ResponseEntity.ok(user);

    }

    @GetMapping("/{id}")
    public ResponseEntity<User> logUser(@PathVariable String id) {
        //first update stats
        LocalDateTime now = LocalDateTime.now();
        statsService.updateStats(id, StatUpdater.login(statsService, id, now));
        // second get user
        var user = service.getInfoById(id);
        return ResponseEntity.ok(user);

    }
}
