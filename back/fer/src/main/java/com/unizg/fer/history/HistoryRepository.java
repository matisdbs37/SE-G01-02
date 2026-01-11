package com.unizg.fer.history;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HistoryRepository extends MongoRepository<HistoryEntry, String> {

    // Find the user's history desc order with pagination
    Page<HistoryEntry> findByUserIdOrderByWatchedAtDesc(String userId, Pageable pageable);

    // Count the number of videos seen by an user
    long countByUserId(String userId);

    // Find all the history_entries by video id
    List<HistoryEntry> findByVideoId(String videoId);

    Optional<HistoryEntry> findByUserIdAndVideoId(String userId, String videoId);

}
