package com.unizg.fer.services;

import com.unizg.fer.categories.Categories;
import com.unizg.fer.categories.CategoriesRepository;
import com.unizg.fer.config.ResourceNotFoundException;

import com.unizg.fer.content.ContentService;
import com.unizg.fer.content.ContentRepository;
import com.unizg.fer.content.ContentCategoriesRepository;
import com.unizg.fer.content.Content;
import com.unizg.fer.content.ContentCategories;
import com.unizg.fer.content.ContentType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the `ContentService` class.
 * Uses Mockito to mock dependencies and verify interactions.
 */
@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @Mock
    private ContentRepository contentRepo;
    @Mock
    private ContentCategoriesRepository contentCategoriesRepo;
    @Mock
    private CategoriesRepository categoriesRepo;

    @InjectMocks
    private ContentService contentService;

    /**
     * Tests that `findAllByType` returns a list of content when content of the specified type is found.
     */
    @Test
    @SuppressWarnings("unchecked")
    void findAllByType_shouldReturnList_whenFound() {
        ContentType type = ContentType.VIDEO;
        List<Content> mockContent = Arrays.asList(new Content(), new Content());

        when(contentRepo.findByType(type.getValue())).thenReturn(mockContent);

        List<Content> result = (List<Content>) ReflectionTestUtils.invokeMethod(contentService, "findAllByType", type);

        assertEquals(2, result.size());
        verify(contentRepo).findByType(type.getValue());
    }

    /**
     * Tests that `findAllByType` throws `ResourceNotFoundException` when no content of the specified type is found.
     */
    @Test
    void findAllByType_shouldThrow_whenEmpty() {
        ContentType type = ContentType.VIDEO;
        when(contentRepo.findByType(anyString())).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> {
            ReflectionTestUtils.invokeMethod(contentService, "findAllByType", type);
        });
    }

    /**
     * Tests that `findByTitle` returns the content when the title is found.
     */
    @Test
    void findByTitle_shouldReturnContent_whenFound() {
        String title = "Java Tutorial";
        Content mockContent = new Content();
        when(contentRepo.findByTitle(title)).thenReturn(Optional.of(mockContent));

        Content result = (Content) ReflectionTestUtils.invokeMethod(contentService, "findByTitle", title);

        assertNotNull(result);
        verify(contentRepo).findByTitle(title);
    }

    /**
     * Tests that `findByTitle` throws `ResourceNotFoundException` when the title is not found.
     */
    @Test
    void findByTitle_shouldThrow_whenNotFound() {
        String title = "Inconnu";
        when(contentRepo.findByTitle(title)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            ReflectionTestUtils.invokeMethod(contentService, "findByTitle", title);
        });
    }

    /**
     * Tests that `findByCategoriesName` returns content when the category name exists and the chain of relationships is complete.
     */
    @Test
    @SuppressWarnings("unchecked")
    void findByCategoriesName_shouldReturnContent_whenChainIsComplete() {
        String catName = "Education";
        String catId = "cat-123";
        String contentId = "content-999";

        Categories mockCategory = new Categories();
        mockCategory.setId(catId);
        ContentCategories mockJoin = new ContentCategories();
        mockJoin.setContentId(contentId);
        Content mockContent = new Content();

        when(categoriesRepo.findByName(catName)).thenReturn(Optional.of(mockCategory));
        when(contentCategoriesRepo.findByCategoryId(catId)).thenReturn(Collections.singletonList(mockJoin));
        when(contentRepo.findAllById(anyList())).thenReturn(Collections.singletonList(mockContent));

        List<Content> result = (List<Content>) ReflectionTestUtils.invokeMethod(contentService, "findByCategoriesName", catName);

        assertEquals(1, result.size());
        verify(categoriesRepo).findByName(catName);
    }

    /**
     * Tests that `findByCategoriesName` throws `ResourceNotFoundException` when the category name does not exist.
     */
    @Test
    void findByCategoriesName_shouldThrow_whenCategoryDoesNotExist() {
        String catName = "Fantome";
        when(categoriesRepo.findByName(catName)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            ReflectionTestUtils.invokeMethod(contentService, "findByCategoriesName", catName);
        });

        verifyNoInteractions(contentCategoriesRepo);
    }

    /**
     * Tests that `findByCategoriesAndType` returns content when the category name and content type exist.
     */
    @Test
    @SuppressWarnings("unchecked")
    void findByCategoriesAndType_shouldReturnContent_whenFound() {
        String catName = "Education";
        String catId = "cat-123";
        ContentType type = ContentType.VIDEO;
        String contentId = "content-999";

        Categories mockCategory = new Categories();
        mockCategory.setId(catId);
        ContentCategories mockJoin = new ContentCategories();
        mockJoin.setContentId(contentId);
        Content mockContent = new Content();

        when(categoriesRepo.findByName(catName)).thenReturn(Optional.of(mockCategory));
        when(contentCategoriesRepo.findByCategoryId(catId)).thenReturn(Collections.singletonList(mockJoin));
        when(contentRepo.findAllByIdAndType(anyList(), eq(type.getValue())))
                .thenReturn(Collections.singletonList(mockContent));

        List<Content> result = (List<Content>) ReflectionTestUtils.invokeMethod(
                contentService,
                "findByCategoriesAndType",
                catName, type
        );

        assertEquals(1, result.size());
        verify(contentRepo).findAllByIdAndType(anyList(), eq(type.getValue()));
    }
}