package com.unizg.fer.content;

import com.unizg.fer.utils.ValidationUtils;

/**
 * Defines different content types 
 */
public enum ContentType {
    AUDIO("audio"),
    VIDEO("video");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ContentType getType(String text) {
       return ValidationUtils.checkTypeStringIntegrity(text);
    }
}
