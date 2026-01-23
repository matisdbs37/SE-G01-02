package com.unizg.fer.history;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unizg.fer.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v2/history")
@Tag(name = "History", description = "APIs for managing user viewing history, progress tracking, ratings and comments")

public class HistoryController {

    private static final String EMAIL_CLAIM = "email";
    private static final String EMAIL_ERROR = "email cannot be empty";

    @Autowired
    HistoryService history;

    @Autowired
    UserService user;

    /***
     * Get paginated User's history
     * 
     * @param jwt   user
     * @param pages number of pages default=0
     * @param size  size of a page default=20
     * @return
     */
    @Operation(summary = "Get user's viewing history", description = "Retrieves paginated viewing history for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email claim", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content)
    })
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public ResponseEntity<Page<HistoryEntry>> getPaginatedHistory(@AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int pages,
            @RequestParam(defaultValue = "20") int size) {

        String email = jwt.getClaim(EMAIL_CLAIM);
        // check claim validity
        if (email.isEmpty() || email.isBlank()) {
            throw new IllegalArgumentException(EMAIL_ERROR);
        }

        var userId = user.getUserByEmail(email).getId();
        return ResponseEntity.ok(history.getPaginatedHistory(userId, pages, size));
    }

    /***
     * Update watchTime from an entry with user and videoId
     * Create a new entry if entry does not exists
     * Need to be called at the start of the video and regulary during the video
     * set watchedAt at now
     * 
     * @param videoId
     * @param Jwt
     * @param watchdTime watched time of the video in s
     */
    @Operation(summary = "Update video watch progress", description = "Updates or creates a history entry with current watch time. Should be called at video start and periodically during playback.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Progress updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters or email claim", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Video not found", content = @Content)
    })
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/{videoId}/progress")
    public void watchVideo(@PathVariable String videoId, @AuthenticationPrincipal Jwt jwt,
            @RequestParam int watchTime) {
        var userId = getUserId(jwt);
        // check if entry exists
        var hist = history.findHistory(userId, videoId);
        history.updateProgress(hist, watchTime);
    }

    /***
     * Update the rating in the historyEntry
     * 
     * @param videoId
     * @param jwt
     * @param stars   must be >= 0 and <= 10
     * @return
     */
    @Operation(summary = "Rate a video", description = "Updates the user's rating for a video (0-10 scale)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HistoryEntry.class))),
            @ApiResponse(responseCode = "400", description = "Invalid rating value (must be 0-10) or email claim", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Video not found", content = @Content)
    })
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/{videoId}/rating")
    public ResponseEntity<HistoryEntry> rateVideo(@PathVariable String videoId, @AuthenticationPrincipal Jwt jwt,
            @RequestParam int stars) {
        var userId = getUserId(jwt);
        // check if entry exists
        var hist = history.findHistory(userId, videoId);
        return ResponseEntity.ok(history.rate(hist, stars));
    }

    @Operation(summary = "Comment on a video", description = "Adds or updates the user's comment on a video")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment added successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HistoryEntry.class))),
            @ApiResponse(responseCode = "400", description = "Invalid comment or email claim", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Video not found", content = @Content)
    })
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/{videoId}/comment")
    public ResponseEntity<HistoryEntry> commentVideo(@PathVariable String videoId, @AuthenticationPrincipal Jwt jwt,
            @RequestParam String text) {
        var userId = getUserId(jwt);
        // check if entry exists
        var hist = history.findHistory(userId, videoId);
        return ResponseEntity.ok(history.addComment(hist, text));
    }

    private String getUserId(Jwt jwt) {
        String email = jwt.getClaim(EMAIL_CLAIM);
        // check claim validity
        if (email.isEmpty() || email.isBlank()) {
            throw new IllegalArgumentException(EMAIL_ERROR);
        }
        return user.getUserByEmail(email).getId();
    }
}
