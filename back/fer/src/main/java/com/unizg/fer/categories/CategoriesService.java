package com.unizg.fer.categories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unizg.fer.config.ResourceNotFoundException;
import com.unizg.fer.utils.ValidationUtils;

@Service
public class CategoriesService {
    private static final String NO_CATEGORIES = "no categories found";

    @Autowired
    public CategoriesRepository repo;

    public List<Categories> getAll() throws ResourceNotFoundException {
        return ValidationUtils.requireNonEmptyList(repo.findAll(), NO_CATEGORIES);
    }
}
