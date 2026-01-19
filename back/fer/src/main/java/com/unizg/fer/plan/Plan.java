package com.unizg.fer.plan;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Tag(name = "Plan", description = "Plan entity used to create plans for the user")
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
