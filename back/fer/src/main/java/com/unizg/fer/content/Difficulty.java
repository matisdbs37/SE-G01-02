package com.unizg.fer.content;

/**
 * Defines different difficulty levels 
 */
public enum Difficulty {
    BEGINNER(1),
    INTERMEDIATE(2),
    ADVANCED(3);

    private final int value;

    Difficulty(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
