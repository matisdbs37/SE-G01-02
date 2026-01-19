package com.unizg.fer.stats;

import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * @author Martin NERON
 *         Class implement UdaterPattern
 *         useful to chain different operation without expecting result (perfect
 *         to update user's statistic)
 */
public class StatUpdater {
    /**
     * Complete login update: increment logins, calculate streak, update last login
     */
    public static Consumer<Stats> login(StatsService service, String userId, LocalDateTime now){
        return stats -> {
            service.incrementLogins(stats);
            service.updateLastLogin(stats, now);
            service.calculateStreak(stats, now);
        };
    }

    /**
     * Update mood checkout date
     */
    public static Consumer<Stats> moodCheckout(StatsService service, LocalDateTime now) {
        return stats -> service.updateMoodCheckout(stats, now);
    }

    /**
     * Only increment logins counter
     */
    public static Consumer<Stats> incrementLogins(StatsService service) {
        return service::incrementLogins;
    }

    /**
     * Only update last login date
     */
    public static Consumer<Stats> updateLastLogin(StatsService service, LocalDateTime now) {
        return stats -> service.updateLastLogin(stats, now);
    }

    /**
     * Only calculate streak
     */
    public static Consumer<Stats> calculateStreak(StatsService service, LocalDateTime now) {
        return stats -> service.calculateStreak(stats, now);
    }

    /**
     * Compose multiple updaters
     */
    @SafeVarargs
    public static Consumer<Stats> compose(Consumer<Stats>... updaters) {
        return stats -> {
            for (Consumer<Stats> updater : updaters) {
                updater.accept(stats);
            }
        };
    }
}
