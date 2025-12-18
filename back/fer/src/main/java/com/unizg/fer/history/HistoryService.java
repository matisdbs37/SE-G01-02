package com.unizg.fer.history;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import com.unizg.fer.config.ResourceNotFoundException;

@Service
public class HistoryService {

    private static final String NO_RESOURCE_FOUND = "history for user %s and video %s was not found ";
    private static final String INVALID_STARS = "the number of stars : %s is invalid";

    @Autowired
    HistoryRepository repo;

    public Page<HistoryEntry> getPaginatedHistory(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findByUserIdOrderByWatchedAtDesc(userId, pageable);
    }

    public HistoryEntry findHistory(String userId, String videoId) {
        return repo.findByUserIdAndVideoId(userId, videoId).orElseThrow(() -> new ResourceNotFoundException(
                String.format(NO_RESOURCE_FOUND, userId, videoId)));
    }

    /***
     * Update the watch time and the watchedAt
     * 
     * @param history   Must be an existing entry
     * @param watchTime
     * @return
     */
    public HistoryEntry updateProgress(HistoryEntry history, int watchTime) {
        var now = LocalDateTime.now();
        history.setWatchedDuration(watchTime);
        history.setWatchedAt(now);
        return repo.save(history);
    }

    /***
     * add a new comment in the comments list of this entry at LocalDate.now()
     * 
     * @param history Must be an existing entry
     * @param text
     * @return
     */
    public HistoryEntry addComment(HistoryEntry history, String text) {
        var now = LocalDate.now();
        history.addComment(text, now);
        return repo.save(history);
    }

    /***
     * Update the rating of the video in the history
     * 
     * @param history Must be an existing entry
     * @param text
     * @return
     */
    public HistoryEntry rate(HistoryEntry history, int stars) {
        // check stars validity
        if (stars < 0 || stars > 10) {
            throw new IllegalArgumentException(String.format(INVALID_STARS, stars));
        }
        history.setRating(stars);
        return repo.save(history);
    }

}
