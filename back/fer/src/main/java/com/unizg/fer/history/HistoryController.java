package com.unizg.fer.history;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unizg.fer.user.UserService;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v2/history")
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
    @PatchMapping("/{videoId}/rating")
    public ResponseEntity<HistoryEntry> rateVideo(@PathVariable String videoId, @AuthenticationPrincipal Jwt jwt,
            @RequestParam int stars) {
        var userId = getUserId(jwt);
        // check if entry exists
        var hist = history.findHistory(userId, videoId);
        return ResponseEntity.ok(history.rate(hist, stars));
    }

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
