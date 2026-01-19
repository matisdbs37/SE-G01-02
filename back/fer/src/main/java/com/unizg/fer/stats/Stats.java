package com.unizg.fer.stats;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Tag(name = "Stats", description = "Stats entity to make statistics on user activity")
@Document(collection = "Stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stats {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("userId")
    private String userId;

    @Builder.Default
    @Field("currentStreak")
    private Integer currentStreak = 0;

    @Builder.Default
    @Field("longestStreak")
    private Integer longestStreak = 0;

    @Builder.Default
    @Field("totalLogins")
    private Integer totalLogins = 0;

    @Field("lastLoginDate")
    private LocalDateTime lastLoginDate;

    @Field("lastMoodCheckoutDate")
    private LocalDateTime lastMoodCheckoutDate;

    @CreatedDate
    @Field("createdAt")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updatedAt")
    private LocalDateTime updatedAt;
}