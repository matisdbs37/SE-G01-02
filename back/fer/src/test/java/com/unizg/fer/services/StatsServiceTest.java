package com.unizg.fer.services;

import com.unizg.fer.config.ResourceNotFoundException;
import com.unizg.fer.stats.Stats;
import com.unizg.fer.stats.StatsRepository;
import com.unizg.fer.stats.StatsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the `StatsService` class.
 * Uses Mockito to mock dependencies and verify interactions.
 */
@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private StatsRepository repo;

    @InjectMocks
    private StatsService statsService;

    /**
     * Tests that `getStatsByUserId` returns the stats when the user ID is found.
     */
    @Test
    void getStatsByUserId_shouldReturnStats_whenFound() {
        String userId = "user123";
        Stats mockStats = Stats.builder().userId(userId).build();

        when(repo.findByUserId(userId)).thenReturn(Optional.of(mockStats));

        Stats result = statsService.getStatsByUserId(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
    }

    /**
     * Tests that `getStatsByUserId` throws `ResourceNotFoundException` when the user ID is not found.
     */
    @Test
    void getStatsByUserId_shouldThrow_whenNotFound() {
        String userId = "unknown";
        when(repo.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> statsService.getStatsByUserId(userId));
    }

    /**
     * Tests that `getStats` returns existing stats when they already exist.
     */
    @Test
    void getStats_shouldReturnExisting_whenExists() {
        String userId = "user123";
        Stats existing = Stats.builder().userId(userId).totalLogins(5).build();

        when(repo.findByUserId(userId)).thenReturn(Optional.of(existing));

        Stats result = statsService.getStats(userId);

        assertEquals(5, result.getTotalLogins());
        verify(repo, never()).save(any());
    }

    /**
     * Tests that `getStats` initializes new stats when they do not exist.
     */
    @Test
    void getStats_shouldInitializeNew_whenNotExists() {
        String userId = "newUser";
        when(repo.findByUserId(userId)).thenReturn(Optional.empty());

        Stats result = statsService.getStats(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(0, result.getTotalLogins());
        assertEquals(0, result.getCurrentStreak());
    }

    /**
     * Tests that `updateStats` applies the provided consumer and saves the updated stats.
     */
    @Test
    void updateStats_shouldApplyConsumerAndSave() {
        String userId = "user123";
        Stats existing = Stats.builder().userId(userId).totalLogins(10).build();

        when(repo.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(repo.save(any(Stats.class))).thenAnswer(i -> i.getArguments()[0]);

        Consumer<Stats> updater = s -> s.setTotalLogins(s.getTotalLogins() + 5);

        Stats result = statsService.updateStats(userId, updater);

        assertEquals(15, result.getTotalLogins());
        verify(repo).save(existing);
    }

    /**
     * Tests that `incrementLogins` increments the total logins and saves the stats.
     */
    @Test
    void incrementLogins_shouldIncrementAndSave() {
        Stats stats = Stats.builder().totalLogins(10).build();

        statsService.incrementLogins(stats);

        assertEquals(11, stats.getTotalLogins());
        verify(repo).save(stats);
    }

    /**
     * Tests that `calculateStreak` initializes the streak on the first login.
     */
    @Test
    void calculateStreak_shouldInitStreak_onFirstLogin() {
        Stats stats = Stats.builder().lastLoginDate(null).build();
        LocalDateTime now = LocalDateTime.now();

        statsService.calculateStreak(stats, now);

        assertEquals(1, stats.getCurrentStreak());
        assertEquals(1, stats.getLongestStreak());
    }

    /**
     * Tests that `calculateStreak` increments the streak on a consecutive day.
     */
    @Test
    void calculateStreak_shouldIncrement_onConsecutiveDay() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        Stats stats = Stats.builder()
                .lastLoginDate(yesterday)
                .currentStreak(5)
                .longestStreak(10)
                .build();
        LocalDateTime now = LocalDateTime.now();

        statsService.calculateStreak(stats, now);

        assertEquals(6, stats.getCurrentStreak());
        assertEquals(10, stats.getLongestStreak());
    }

    /**
     * Tests that `calculateStreak` updates the longest streak when a new record is achieved.
     */
    @Test
    void calculateStreak_shouldUpdateLongest_whenRecordBeaten() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        Stats stats = Stats.builder()
                .lastLoginDate(yesterday)
                .currentStreak(10)
                .longestStreak(10)
                .build();
        LocalDateTime now = LocalDateTime.now();

        statsService.calculateStreak(stats, now);

        assertEquals(11, stats.getCurrentStreak());
        assertEquals(11, stats.getLongestStreak());
    }

    /**
     * Tests that `calculateStreak` resets the streak when the gap between logins is more than one day.
     */
    @Test
    void calculateStreak_shouldReset_whenGapIsMoreThanOneDay() {
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        Stats stats = Stats.builder()
                .lastLoginDate(twoDaysAgo)
                .currentStreak(50)
                .longestStreak(50)
                .build();
        LocalDateTime now = LocalDateTime.now();

        statsService.calculateStreak(stats, now);

        assertEquals(1, stats.getCurrentStreak());
        assertEquals(50, stats.getLongestStreak());
    }

    /**
     * Tests that `calculateStreak` resets the streak when the login occurs on the same day.
     */
    @Test
    void calculateStreak_shouldReset_whenSameDay() {
        LocalDateTime earlierToday = LocalDateTime.now().minusHours(2);
        Stats stats = Stats.builder()
                .lastLoginDate(earlierToday)
                .currentStreak(10)
                .longestStreak(10)
                .build();
        LocalDateTime now = LocalDateTime.now();

        statsService.calculateStreak(stats, now);

        assertEquals(1, stats.getCurrentStreak());
        assertEquals(1, stats.getLongestStreak());
    }

    /**
     * Tests that `updateLastLogin` sets the last login date to the provided date.
     */
    @Test
    void updateLastLogin_shouldSetDate() {
        Stats stats = new Stats();
        LocalDateTime now = LocalDateTime.now();

        statsService.updateLastLogin(stats, now);

        assertEquals(now, stats.getLastLoginDate());
    }

    /**
     * Tests that `updateMoodCheckout` sets the last mood checkout date to the provided date.
     */
    @Test
    void updateMoodCheckout_shouldSetDate() {
        Stats stats = new Stats();
        LocalDateTime now = LocalDateTime.now();

        statsService.updateMoodCheckout(stats, now);

        assertEquals(now, stats.getLastMoodCheckoutDate());
    }
}