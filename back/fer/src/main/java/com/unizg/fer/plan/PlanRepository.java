package com.unizg.fer.plan;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends MongoRepository<Plan, String> {

    // must be stream to not load all at once
    @Query("{}")
    Stream<Plan> streamAll();

    List<Plan> findAllByUserId(String userId);

}
