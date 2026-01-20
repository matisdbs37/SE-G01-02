package com.unizg.fer.content;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

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

    @Field(targetType = FieldType.OBJECT_ID, name = "contentId")
    private String contentId;

    @Field(targetType = FieldType.OBJECT_ID, name = "categoryId")
    private String categoryId;
}
