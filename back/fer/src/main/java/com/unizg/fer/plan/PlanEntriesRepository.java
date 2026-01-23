package com.unizg.fer.plan;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlanEntriesRepository extends MongoRepository<PlanEntry, String>{

}
