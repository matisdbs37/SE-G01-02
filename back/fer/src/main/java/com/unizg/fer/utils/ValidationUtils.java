package com.unizg.fer.utils;

import java.util.List;

import com.unizg.fer.config.ResourceNotFoundException;
import com.unizg.fer.content.ContentType;

/**
 * @author Martin NERON
 *         Utils class to handle basic validation on data from the repository
 */
public class ValidationUtils {

    /**
     * Ensure that the list isn't empty
     * 
     * @return List
     * @throws ResourceNotFoundException if list == null or is empty
     */
    public static <T> List<T> requireNonEmptyList(List<T> list, String errorMessage) throws ResourceNotFoundException {
        // handle empty lisy with ResourceNotFoundException
        if (list == null || list.isEmpty()) {
            throw new ResourceNotFoundException(errorMessage);
        }
        return list;
    }

    /**
     * Check if the string passed correspond to type from the ContentType enum
     * the String type is accepted if equals ignore case the Enum name
     * exempl video = VidEo
     * 
     * @return true if the string is compatile
     * @throws IllegalArgumentException
     */
    public static ContentType checkTypeStringIntegrity(String type) throws IllegalArgumentException {
        // check if type is null
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("the type cannot be null");
        }
        String normalizedType = type.trim().toLowerCase();
        for (ContentType t : ContentType.values()) {
            if (t.getValue().equals(normalizedType)) {
                return t;
            }
        }
        // String does not match any ContentType
        throw new IllegalArgumentException("the type : " + type + "does not match any content type");
    }

}
