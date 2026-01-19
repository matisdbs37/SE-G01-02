package com.unizg.fer.controller;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v2")
public class LogoutController {
     @GetMapping("/logout")
    public Map<String, Object> error() {
        return Map.of(
            "logout", "TODO: do something with the tokens ?"
        );
    }
}
