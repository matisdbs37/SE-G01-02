package com.unizg.fer.plan;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Document(collection = "Plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    private String id;

    @NonNull
    private String userId;

    @NonNull
    private PlanLevel level;

    @NonNull
    private List<PlanEntry> toWatch;

    @NonNull
    private LocalDateTime createdAt;

}
