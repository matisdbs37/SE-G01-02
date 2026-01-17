package com.unizg.fer.content;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.unizg.fer.categories.CategoriesRepository;
import com.unizg.fer.config.ResourceNotFoundException;
import com.unizg.fer.utils.ValidationUtils;

@Service
@SuppressWarnings("unused")
public class ContentService {

    private static final String CONTENT_NOT_FOUND = "no content found with id %s ";
    private static final String CATEGORY_NOT_FOUND = "no category found with id %s";
    private static final String NO_CONTENT_OF_TYPE = "no content of type : ";
    private static final String NO_CONTENT_TITLE = "no content with title : ";
    private static final String NO_CATEGORY_NAME = "no category with name : ";
    private static final String NO_CONTENT_FOR_CATEGORY = "no content for this category : ";

    @Autowired
    public ContentRepository contentRepo;

    @Autowired
    public ContentCategoriesRepository contentCategoriesRepo;

    @Autowired
    public CategoriesRepository categoriesRepo;

    List<Content> findAllByType(ContentType type) throws ResourceNotFoundException {
        var typeName = type.getValue();
        var res = ValidationUtils.requireNonEmptyList(contentRepo.findByType(typeName), NO_CONTENT_OF_TYPE + typeName);
        return res;
    }

    /**
     * Find a content by title
     * 
     * @throws ResourceNotFoundException
     */
    Content findByTitle(String title) throws ResourceNotFoundException {
        return contentRepo.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException(NO_CONTENT_TITLE + title));
    }

    /**
     * Find all content by categoriy names
     * 
     * @throws ResourceNotFoundException
     */
    List<Content> findByCategoriesName(String name) throws ResourceNotFoundException {
        // get category id
        var categoryId = getCategoryIdFromName(name);
        // get all content with this category name
        var join = ValidationUtils.requireNonEmptyList(contentCategoriesRepo.findByCategoryId(categoryId),
                NO_CATEGORY_NAME);
        // extract all contents ID from this ContentCategory join
        var contentIds = join.stream().map(ContentCategories::getContentId).collect(Collectors.toList());
        // get all content for this ids
        return ValidationUtils.requireNonEmptyList(contentRepo.findAllById(contentIds), NO_CONTENT_FOR_CATEGORY);
    }

    /**
     * Find all content by categoriy names
     * 
     * @throws ResourceNotFoundException
     */
    List<Content> findByCategoriesAndType(String name, ContentType type) throws ResourceNotFoundException {
        // get category id
        var categoryId = getCategoryIdFromName(name);
        // get all content with this category name
        var join = ValidationUtils.requireNonEmptyList(contentCategoriesRepo.findByCategoryId(categoryId),
                NO_CATEGORY_NAME);
        // extract all contents ID from this ContentCategory join
        var contentIds = join.stream().map(ContentCategories::getContentId).collect(Collectors.toList());
        // get all content for this ids and type
        return ValidationUtils.requireNonEmptyList(contentRepo.findAllByIdAndType(contentIds, type.getValue()),
                NO_CONTENT_FOR_CATEGORY);
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Content> findRandomByType(int number, String type) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("type").is(type)),
                Aggregation.sample(number));

        return mongoTemplate.aggregate(
                aggregation,
                "Content",
                Content.class).getMappedResults();
    }

    public Page<Content> findAll(int pages, int size) {
        Pageable pageable = PageRequest.of(pages, size);
        return contentRepo.findAll(pageable);
    }

    public Content createContent(Content content) {
        return contentRepo.save(content);
    }

    public Content updateContent(String id, Content content) {
        return contentRepo.findById(id)
                .map(existing -> {
                    existing.setTitle(content.getTitle());
                    existing.setType(content.getType());
                    existing.setDurationMin(content.getDurationMin());
                    existing.setDifficulty(content.getDifficulty());
                    existing.setLanguage(content.getLanguage());
                    existing.setSource(content.getSource());
                    existing.setIsActive(content.getIsActive());
                    return contentRepo.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException(String.format(CONTENT_NOT_FOUND, id)));
    }

    public void deleteContent(String id) {
        contentRepo.deleteById(id);
    }

    private String getCategoryIdFromName(String name) throws ResourceNotFoundException {
        var category = categoriesRepo.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(NO_CATEGORY_NAME));
        return category.getId();
    }

    public List<ContentCategories> findAllCategories() {
        return contentCategoriesRepo.findAll();

    }

    public ContentCategories updateCategory(String id, ContentCategories contentCategories) {
        return contentCategoriesRepo.findById(id)
                .map(existing -> {
                    existing.setContentId(contentCategories.getContentId());
                    existing.setCategoryId(contentCategories.getCategoryId());
                    return contentCategoriesRepo.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException(String.format(CATEGORY_NOT_FOUND, id)));
    }

    public ContentCategories createCategory(ContentCategories contentCategories) {
        return contentCategoriesRepo.save(contentCategories);
    }

    public void deleteCategory(String id) {
        contentCategoriesRepo.deleteById(id);
    }

}
