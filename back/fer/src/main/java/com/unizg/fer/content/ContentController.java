package com.unizg.fer.content;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/content")
@Tag(name = "Content Management", description = "Endpoints for managing and retrieving Audio and Video content")
public class ContentController {

    @Autowired
    private ContentService service;

    @Operation(summary = "Get content by type", description = "Retrieves a list of content filtered by type. Supported values: AUDIO, VIDEO (Case-insensitive).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(array = @ArraySchema(schema = @Schema(implementation = com.unizg.fer.content.Content.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid type provided"),
            @ApiResponse(responseCode = "404", description = "No content found for this type")
    })
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/type/{text}")
    public ResponseEntity<List<com.unizg.fer.content.Content>> getContentByType(@PathVariable String text) {
        // Service handles case-insensitivity
        var type = ContentType.getType(text);
        return ResponseEntity.ok(service.findAllByType(type));
    }

    @Operation(summary = "Get content by title", description = "Retrieves the details of a specific content item using its title.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content found", content = @Content(schema = @Schema(implementation = com.unizg.fer.content.Content.class))),
            @ApiResponse(responseCode = "404", description = "Content not found")
    })
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{title}")
    public ResponseEntity<com.unizg.fer.content.Content> getContentById(@PathVariable String title) {
        return ResponseEntity.ok(service.findByTitle(title));
    }

    @Operation(summary = "List all content (Paginated)", description = "Retrieves a paginated list of all available media content.")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/all")
    public ResponseEntity<Page<com.unizg.fer.content.Content>> getAllContent(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int pages,
            @Parameter(description = "The size of the page to be returned") @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.findAll(pages, size));
    }

    @Operation(summary = "Get categories", description = "Retrieves all associations between content items and categories.")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/categories")
    public ResponseEntity<List<ContentCategories>> getAllCategories() {
        return ResponseEntity.ok(service.findAllCategories());
    }

    @Operation(summary = "Add new content", description = "Creates a new content entry. Type must be AUDIO or VIDEO.")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<com.unizg.fer.content.Content> addContent(
            @RequestBody com.unizg.fer.content.Content content) {
        return ResponseEntity.ok(service.createContent(content));
    }

    @Operation(summary = "Update content", description = "Updates an existing content item's information.")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{contentId}")
    public ResponseEntity<com.unizg.fer.content.Content> updateContent(
            @PathVariable String contentId,
            @RequestBody com.unizg.fer.content.Content content) {
        return ResponseEntity.ok(service.updateContent(contentId, content));
    }

    @Operation(summary = "Delete content", description = "Permanently removes a content item from the database.")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{contentId}")
    public ResponseEntity<Void> deleteContent(@PathVariable String contentId) {
        service.deleteContent(contentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Assign category", description = "Links a specific content item to a category.")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/categories")
    public ResponseEntity<ContentCategories> createContentCategories(@RequestBody ContentCategories contentCategories) {
        return ResponseEntity.ok(service.createCategory(contentCategories));
    }

    @Operation(summary = "Update category link", description = "Updates an existing content-category association.")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/categories/{id}")
    public ResponseEntity<ContentCategories> updateContentCategories(
            @PathVariable String id,
            @RequestBody ContentCategories contentCategories) {
        return ResponseEntity.ok((ContentCategories) service.updateCategory(id, contentCategories));
    }

    @Operation(summary = "Remove category link", description = "Deletes a content-category association by its unique ID.")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteContentCategoriesById(@PathVariable String id) {
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}