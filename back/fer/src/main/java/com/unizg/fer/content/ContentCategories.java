package com.unizg.fer.content;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Content_categories")
@SuppressWarnings("unused")
@Getter
@Setter
public class ContentCategories {
    @Id
    private String id;

    private String contentId;

    private String categoryId;
}
