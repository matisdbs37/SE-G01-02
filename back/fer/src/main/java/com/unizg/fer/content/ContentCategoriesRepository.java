package com.unizg.fer.content;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentCategoriesRepository extends MongoRepository<ContentCategories, String> {
    
    List<ContentCategories> findByCategoryId(String categoryId);
    
}
