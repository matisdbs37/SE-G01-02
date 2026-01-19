package com.unizg.fer.content;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentRepository extends MongoRepository<Content, String> {

    Optional<Content> findByTitle(String title);

    List<Content> findByType(String type);

    List<Content> findByTypeAndTitle(String type, String title);

    List<Content> findByTypeAndLanguage(String type, String language);

    List<Content> findByLanguage(String language);

    List<Content> findByIsActiveTrue();

    List<Content> findByDifficulty(Integer difficulty);

    List<Content> findAllByIdAndType(Iterable<String> ids, String type);

    //List<Content> findRandomByType(int number, String type);

}
