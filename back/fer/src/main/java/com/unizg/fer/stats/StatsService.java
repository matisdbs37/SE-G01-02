package com.unizg.fer.stats;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unizg.fer.config.ResourceNotFoundException;

@Service
public class StatsService {

    @Autowired
    StatsRepository repo;

    private static final String STAT_NOT_FOUND = "stat not found for user : ";

    public Stats getStatsByUserId(String userId) {
        return repo.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException(STAT_NOT_FOUND + userId));
    }

    /**
     * Update stats with a custom consumer (use full when only 1 consumer needed )
     */
    public Stats updateStats(String userId, Consumer<Stats> updater) {
        Stats stats = getOrCreateStats(userId);
        updater.accept(stats);
        return repo.save(stats);
    }

    /**
     * Get the user's statistics and create one if not found
     */
    public Stats getStats(String userId) {
        return getOrCreateStats(userId);
    }

    /**
     * Initialize user's statistics if not created
     */
    private Stats initializeStats(String userId) {
        return Stats.builder()
                .userId(userId)
                .currentStreak(0)
                .longestStreak(0)
                .totalLogins(0)
                .build();
    }

    /**
     * Get existing stats or create new ones
     */
    private Stats getOrCreateStats(String userId) {
        return repo.findByUserId(userId)
                .orElseGet(() -> initializeStats(userId));
    }

    public void incrementLogins(Stats stats) {
        if (stats == null) {
            throw new IllegalArgumentException("Stats cannot be null");
        }
        stats.setTotalLogins(stats.getTotalLogins() + 1);
        repo.save(stats);
    }

    /**
     * Update Current and longuest streak
     */
    public void calculateStreak(Stats stats, LocalDateTime now) {
        if (stats == null || now == null) {
            throw new IllegalArgumentException("Stats and date cannot be null");
        }

        // if first connexion
        if (stats.getLastLoginDate() == null) {
            stats.setCurrentStreak(1);
            stats.setLongestStreak(1);
            return;
        }
        // count in days the time between now and last login date
        long daysSinceLastLogin = ChronoUnit.DAYS.between(stats.getLastLoginDate(), now);

        // Put current and longuest streak at 1
        if (daysSinceLastLogin == 0) {
            stats.setCurrentStreak(1);
            stats.setLongestStreak(1);
        }
        // user is in a streak
        if (daysSinceLastLogin == 1) {
            stats.setCurrentStreak(stats.getCurrentStreak() + 1);
            // update longuest streak if needed
            if (stats.getCurrentStreak() > stats.getLongestStreak()) {
                stats.setLongestStreak(stats.getCurrentStreak());
            }
        }
        // else break streak
        if (daysSinceLastLogin > 1) {
            stats.setCurrentStreak(1);
        }

    }

    public void updateLastLogin(Stats stats, LocalDateTime now) {
        stats.setLastLoginDate(now);

    }

    public void updateMoodCheckout(Stats stats, LocalDateTime now) {
        stats.setLastMoodCheckoutDate(now);

    }

}
