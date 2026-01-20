package com.unizg.fer.history;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import com.unizg.fer.config.ResourceNotFoundException;
import com.unizg.fer.content.ContentRepository;

@Service
public class HistoryService {

    private static final String NO_RESOURCE_FOUND = "history for user %s and video %s was not found ";
    private static final String INVALID_STARS = "the number of stars : %s is invalid";
    private static final String NO_CONTENT_FOUND = "no content with id : %s";
    private static final String ILLEGAL_ARGUMENTS = "illegal argument for userId : %s or videoId : %s";

    @Autowired
    HistoryRepository repo;

    @Autowired
    ContentRepository contentRepo;

    public Page<HistoryEntry> getPaginatedHistory(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findByUserIdOrderByWatchedAtDesc(userId, pageable);
    }

    /**
     * Find or create history
     * 
     * @param userId
     * @param contentId
     * @return an actual history or a new one
     */
    public HistoryEntry findHistory(String userId, String contentId) {
        // check user id && video id for safety
        if (userId == null || userId.isEmpty() || contentId.isEmpty() || contentId == null) {
            throw new IllegalArgumentException(String.format(ILLEGAL_ARGUMENTS, contentId, userId));
        }
        return repo.findByUserIdAndContentId(userId, contentId).orElseGet(() -> {
            // get vido or else throw video not found error
            var content = contentRepo.findById(contentId).orElseThrow(() -> new IllegalArgumentException(
                    String.format(NO_CONTENT_FOUND, contentId)));
            var now = LocalDateTime.now();
            HistoryEntry newEntry = new HistoryEntry();
            newEntry.setUserId(userId);
            newEntry.setContentId(contentId);
            newEntry.setWatchedAt(now);
            newEntry.setContentDuration(content.getDurationMin());
            newEntry.setWatchedDuration(0);
            newEntry.setRating(null);
            newEntry.setComments(null);
            newEntry.setCreatedAt(now);
            return repo.save(newEntry);
        });
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

    /**
     * find all the interaction with this content.
     * 
     * @param contentId
     * @return
     */
    public List<HistoryEntry> getAllContentInteraction(String id) {
        // check if content exists
        if (!contentRepo.existsById(id)) {
            throw new ResourceNotFoundException(String.format(NO_CONTENT_FOUND, id));
        }

        var contentId = id;
        return repo.findByContentId(contentId);
    }

    /**
     * Computes the mean rating of this content.
     * 
     * @param contentId
     * @return average or -1 if no one rated the content 
     */
    public Double getRating(String contentId) {
        return getAllContentInteraction(contentId)
                .stream()
                .filter(entry -> entry.getRating() != null)
                .mapToInt(HistoryEntry::getRating).average().orElse(-1);
    }
}
