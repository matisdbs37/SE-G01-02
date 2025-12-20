package com.unizg.fer.emailManager;

public enum JSONValues {
    USER_NAME("userName"),
    ACTUAL_STREAK("actualStreak"),
    EXTENDED_STREAK("extendedStreak");

    private final String value;

    private JSONValues(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
