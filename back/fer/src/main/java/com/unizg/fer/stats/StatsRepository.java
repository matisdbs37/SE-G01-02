package com.unizg.fer.stats;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StatsRepository extends MongoRepository<Stats, String> {

    Optional<Stats> findByUserId(String userId);

    // must be stream to not load all at once
    @Query("{}")
    Stream<Stats> streamAll();
}