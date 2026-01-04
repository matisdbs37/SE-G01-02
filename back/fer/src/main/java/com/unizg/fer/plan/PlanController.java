package com.unizg.fer.plan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.unizg.fer.user.UserService;

@Controller
@RequestMapping("/plan/")
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
    @PostMapping("/{level}")
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
    @GetMapping
    @ResponseBody
    public ResponseEntity<Iterable<Plan>> getMyPlans(@AuthenticationPrincipal Jwt jwt) {
        var userId = user.getUserByEmail(jwt.getClaim("email")).getId();
        Iterable<Plan> plans = manager.getAllFromUser(userId);
        return ResponseEntity.ok(plans);

    }
}
