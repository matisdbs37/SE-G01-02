package com.unizg.fer.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

	@GetMapping("/api/v2/login")
	public ResponseEntity<?> login(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaim("email");
        String sub = jwt.getSubject();
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("userId", sub);
        response.put("claims", jwt.getClaims());
        System.out.println("test");
        return ResponseEntity.ok(response);
	}

	public record Message(String message) {
	}
}
