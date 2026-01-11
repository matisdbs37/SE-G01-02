package com.unizg.fer.content;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v2/content")
@Tag(name = "Content", description = "APIs for retrieving content")

public class ContentController {

    @Autowired
    ContentService service;

    @Operation(summary = "Get content by type", description = "Retrieves all content filtered by type (e.g., VIDEO, ARTICLE, COURSE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content retrieved successfully", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @Schema(implementation = Content.class))),
            @ApiResponse(responseCode = "400", description = "Invalid content type", content = @io.swagger.v3.oas.annotations.media.Content),
            @ApiResponse(responseCode = "404", description = "No content found for this type", content = @io.swagger.v3.oas.annotations.media.Content)
    })
    @GetMapping("/type/{text}")
    public ResponseEntity<List<Content>> getContentByType(@PathVariable String text) {
        var type = ContentType.getType(text);
        var contents = service.findAllByType(type);
        return ResponseEntity.ok(contents);
    }

    @Operation(summary = "Get content by title", description = "Retrieves a specific content item by its title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content found successfully", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @Schema(implementation = Content.class))),
            @ApiResponse(responseCode = "404", description = "Content not found", content = @io.swagger.v3.oas.annotations.media.Content)
    })
    @GetMapping("/{title}")
    public ResponseEntity<Content> getContentById(@PathVariable String title) {
        var content = service.findByTitle(title);
        return ResponseEntity.ok(content);
    }

}
