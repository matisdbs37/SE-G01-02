package com.unizg.fer.plan;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Document(collection = "Plan_Entries")
@Data
@Builder
public class PlanEntry {

    @Id
    private String id;

    @NonNull
    private ObjectId content;

    /**
     * true if api notified the user
     */
    @Builder.Default
    private boolean notified = false;
}
