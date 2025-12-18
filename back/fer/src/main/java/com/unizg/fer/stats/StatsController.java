package com.unizg.fer.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/stats")
public class StatsController {

    @Autowired
    StatsService service;

    @RequestMapping("/{userId}")
    public ResponseEntity<Stats> getStats(@PathVariable String userId) {
        return ResponseEntity.ok(service.getStatsByUserId(userId));
    }

    
}
