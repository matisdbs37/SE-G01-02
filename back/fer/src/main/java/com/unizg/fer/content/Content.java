package com.unizg.fer.content;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Content entity representing the media that the api can provide, video or audio")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Content")
@SuppressWarnings("unused")
@Getter
@Setter
public class Content {
    @Id
    private String id;

    @NotBlank(message = "Title is required")
    @Field("title")
    private String title;

    @NotNull(message = "Type is required")
    @Field("type")
    private String type;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Field("durationMin")
    private Integer durationMin;

    @Field("difficulty")
    private Integer difficulty;

    @NotBlank(message = "Language is required")
    @Field("language")
    private String language;

    @Field("source")
    private String source;

    @Field("isActive")
    private Boolean isActive;

    @NotNull(message = "Created date is required")
    @Field("createdAt")
    private LocalDateTime createdAt;

}
