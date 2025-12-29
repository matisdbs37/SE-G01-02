package com.unizg.fer.services;

import com.unizg.fer.categories.Categories;
import com.unizg.fer.categories.CategoriesRepository;
import com.unizg.fer.categories.CategoriesService;
import com.unizg.fer.config.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the `CategoriesService` class.
 * Uses Mockito to mock dependencies and verify interactions.
 */
@ExtendWith(MockitoExtension.class)
class CategoriesServiceTest {

    @Mock
    private CategoriesRepository repo;

    @InjectMocks
    private CategoriesService categoriesService;

    /**
     * Tests that `getAll` returns a list of categories when the repository is not empty.
     *
     * @throws ResourceNotFoundException if no categories are found
     */
    @Test
    void getAll_shouldReturnCategories_whenListIsNotEmpty() throws ResourceNotFoundException {
        Categories cat1 = new Categories();
        Categories cat2 = new Categories();
        List<Categories> mockList = Arrays.asList(cat1, cat2);

        when(repo.findAll()).thenReturn(mockList);

        List<Categories> result = categoriesService.getAll();

        assertEquals(2, result.size());
        assertEquals(mockList, result);

        verify(repo, times(1)).findAll();
    }

    /**
     * Tests that `getAll` throws `ResourceNotFoundException` when the repository is empty.
     */
    @Test
    void getAll_shouldThrowResourceNotFoundException_whenListIsEmpty() {

        when(repo.findAll()).thenReturn(Collections.emptyList());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoriesService.getAll();
        });

        assertEquals("no categories found", exception.getMessage());

        verify(repo, times(1)).findAll();
    }
}