package com.unizg.fer.categories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoriesRepository extends MongoRepository<Categories, String> {

    public Optional<Categories> findByName(String name);

    /**
     * get all categories by id in this list
     */
    List<Categories> findAllByIdIn(List<String> ids);

}
