package com.unizg.fer.stats;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unizg.fer.config.ResourceNotFoundException;
import com.unizg.fer.emailManager.EmailService;
import com.unizg.fer.emailManager.JSONValues;
import com.unizg.fer.emailManager.TemplateType;

@Service
public class StatsService {

    @Autowired
    StatsRepository repo;

    @Autowired
    private EmailService emailService;

    private static final String STAT_NOT_FOUND = "stat not found for user : ";

    private static final Logger LOGGER = LoggerFactory.getLogger(StatsService.class);

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

    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional(readOnly = true)
    public void checkAndNotifyUserEngagement() {
        LocalDateTime now = LocalDateTime.now();
        try (Stream<Stats> stream = repo.streamAll()) {
            stream.forEach(stats -> {
                if (stats.getLastLoginDate() == null)
                    return;
                long daysSinceLastLogin = ChronoUnit.DAYS.between(stats.getLastLoginDate(), now);
                if (daysSinceLastLogin == 1) {
                    sendStreakReminder(stats);
                }
                if (daysSinceLastLogin >= 7) {
                    sendInactivityEmail(stats, daysSinceLastLogin);
                }
            });
        } catch (Exception e) {
            // non blocking error
            LOGGER.error("error while verifing user engagement", e);
        }
    }

    private void sendStreakReminder(Stats stats) {
        Map<JSONValues, String> values = new HashMap<>();
        values.put(JSONValues.USER_NAME, stats.getUserId());
        values.put(JSONValues.ACTUAL_STREAK, String.valueOf(stats.getCurrentStreak()));
        values.put(JSONValues.EXTENDED_STREAK, String.valueOf(stats.getCurrentStreak() + 1));

        emailService.sendEmail(stats.getUserId(), TemplateType.STREAK, values);
    }

    private void sendInactivityEmail(Stats stats, long daysInactive) {
        Map<JSONValues, String> values = new HashMap<>();
        values.put(JSONValues.USER_NAME, stats.getUserId());
        values.put(JSONValues.DAYS_INACTIVE, String.valueOf(daysInactive));
        values.put(JSONValues.LAST_LOGIN_DATE, stats.getLastLoginDate().toString());

        emailService.sendEmail(stats.getUserId(), TemplateType.INACTIVE, values);
    }

}
