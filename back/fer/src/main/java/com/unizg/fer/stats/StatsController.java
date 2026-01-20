package com.unizg.fer.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unizg.fer.plan.PlanManager;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v2/stats")
public class StatsController {

    @Autowired
    StatsService service;

    @Operation(summary = "Get user statistics", description = "Retrieves comprehensive statistics for a specific user including login history, activity metrics, and learning progress")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Stats.class))),
            @ApiResponse(responseCode = "404", description = "User not found or no statistics available", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @RequestMapping("/{userId}")
    public ResponseEntity<Stats> getStats(@PathVariable String userId) {
        return ResponseEntity.ok(service.getStatsByUserId(userId));
    }
}
