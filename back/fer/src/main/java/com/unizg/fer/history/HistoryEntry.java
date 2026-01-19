package com.unizg.fer.history;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Schema(name = "History", description = "History entity to track user activity on the api")
@Document(collection = "History_entries")
@CompoundIndex(name = "user_video", def = "{'userId': 1, 'videoId': 1}", unique = true)
@Data
@Builder
@AllArgsConstructor
public class HistoryEntry {

    @Id
    private String id;

    @Indexed
    @Field("userId")
    private String userId;

    @Indexed
    @Field("videoId")
    private String videoId;

    @Indexed
    @Field("watchedAt")
    private LocalDateTime watchedAt;

    @Field("contentDuration")
    private Integer contentDuration; // in seconds

    @Field("watchedDuration")
    private Integer watchedDuration; // in seconds

    @Field("rating")
    private Integer rating; // 1-10 to five stars

    @Field("comments")
    private List<CommentsEntry> comments;

    @CreatedDate
    @Field("createdAt")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updatedAt")
    private LocalDateTime updatedAt;

    public void addComment(String text, LocalDate now) {
        comments.add(new CommentsEntry(text, now));
    }

    public HistoryEntry() {
        this.comments = new ArrayList<CommentsEntry>();
    }
}