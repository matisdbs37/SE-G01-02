package com.unizg.fer.plan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.unizg.fer.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/api/v2/plan/")
@Tag(name = "Learning Plans", description = "APIs for managing personalized learning plans")

public class PlanController {

        @Autowired
        private PlanManager manager;

        @Autowired
        private UserService user;

        /**
         * Create a new plan for the authenticated user
         * POST /plan/{level}
         * Example: POST /plan/EASY
         * 
         * @param jwt   authenticated user JWT token
         * @param level plan difficulty level from path
         */
        @Operation(summary = "Create a new learning plan", description = "Creates a new personalized learning plan for the authenticated user with specified difficulty level")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Plan created successfully", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Plan EASY created successfully"))),
                        @ApiResponse(responseCode = "400", description = "Invalid plan level", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Plan already exists for this level", content = @Content)
        })
        @PreAuthorize("hasAuthority('ROLE_USER')")
        @PostMapping("{level}")
        @ResponseBody
        public ResponseEntity<String> createPlan(
                        @AuthenticationPrincipal Jwt jwt,
                        @PathVariable PlanLevel level) {
                var userId = user.getUserByEmail(jwt.getClaim("email")).getId();
                manager.createPlan(userId, level);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body("Plan " + level + " created successfully");
        }

        /**
         * Get all plans for the authenticated user
         * GET /plan
         * 
         * @param jwt authenticated user JWT token
         */
        @Operation(summary = "Get user's learning plans", description = "Retrieves all learning plans created by the authenticated user should be  EASY,  INTERMEDIATE or ADVANCED")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Plans retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Plan.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
                        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
        })
        @PreAuthorize("hasAuthority('ROLE_USER')")
        @GetMapping
        @ResponseBody
        public ResponseEntity<Iterable<Plan>> getMyPlans(@AuthenticationPrincipal Jwt jwt) {
                var userId = user.getUserByEmail(jwt.getClaim("email")).getId();
                Iterable<Plan> plans = manager.getAllFromUser(userId);
                return ResponseEntity.ok(plans);

        }
}
