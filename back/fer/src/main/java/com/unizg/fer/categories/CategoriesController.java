package com.unizg.fer.categories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v2/categories")
@Tag(name = "Categories", description = "APIs for managing content categories")

public class CategoriesController {

    @Autowired
    public CategoriesService service;

    @Operation(summary = "Get all categories", description = "Retrieves a complete list of all available content categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categories.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/")
    public ResponseEntity<List<Categories>> getAllCategories() {
        var categories = service.getAll();
        return ResponseEntity.ok(categories);
    }

}
