package com.unizg.fer.emailManager;

public enum JSONValues {
    USER_NAME("userName"),
    ACTUAL_STREAK("actualStreak"),
    EXTENDED_STREAK("extendedStreak"),
    VIDEO_TITLE("videoTitle"), 
    VIDEO_DURATION("videoDuration"), 
    DAYS_INACTIVE("daysInactive"), 
    LAST_LOGIN_DATE("lastLoginDate"); 

    private final String value;

    private JSONValues(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
