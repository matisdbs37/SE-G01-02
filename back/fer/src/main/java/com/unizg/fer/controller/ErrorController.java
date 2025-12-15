package com.unizg.fer.controller;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v2")
public class ErrorController {
    @GetMapping("/error")
    public Map<String, Object> error() {
        return Map.of(
            "error", "Something went wrong"
        );
    }

}
