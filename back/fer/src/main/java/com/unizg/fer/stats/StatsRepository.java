package com.unizg.fer.stats;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatsRepository extends MongoRepository<Stats, String> {

    Optional<Stats> findByUserId(String userId);
}