package com.unizg.fer.categories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoriesRepository extends MongoRepository<Categories, String> {

    public Optional<Categories> findByName(String name);
}
