package com.unizg.fer.security.rbac;

import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import org.springframework.core.convert.converter.Converter;

/**
 * Converts string to ENUM Role @see Role.java
 */
@Component
@ReadingConverter
public class StringToRoleConverter implements Converter<String, Role> {

    private static final String ILLEGAL_ARGS = "role cannot be empty 'ROLE_USER' or 'ROLE_ADMIN'";
    private static final String UNKNOWN_ROLE = "Unknown role value: %s";

    @Override
    @NonNull
    public Role convert(String source) {
        if (source == null || source.isEmpty()) {
            throw new IllegalArgumentException(ILLEGAL_ARGS);
        }
        var cleaned = source.trim().toUpperCase();

        // iterate on enum values
        for (Role role : Role.values()) {
            if (role.name().equals(cleaned) || role.getValue().equalsIgnoreCase(cleaned)) {
                return role;
            }
        }
        throw new IllegalArgumentException(String.format(UNKNOWN_ROLE, cleaned));
    }

}
