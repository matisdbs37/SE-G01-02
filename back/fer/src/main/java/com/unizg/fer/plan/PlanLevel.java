package com.unizg.fer.plan;

/***
 * Enum to quantify the number of videos in a plan 
 */
public enum PlanLevel {
    EASY(3),
    INTERMEDIATE(7),
    ADVANCED(10);

    private final int value;

    PlanLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
