package com.unizg.fer.user;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a user entity stored in the MongoDB database.
 * Contains user details such as email, name, roles, and various attributes.
 */
@Schema(description = "User entity representing application users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Users")
@Getter
@Setter
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    @NotNull(message = "Email is required")
    @Field("email")
    private String email;

    @NotNull(message = "First Name is required")
    @Field("first_Name")
    private String firstName;

    @NotNull(message = "Last Name is required")
    @Field("last_Name")
    private String lastName;

    @NotNull(message = "Role is required")
    @Field("roles")
    private String roles;

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("status")
    private Boolean isActive;

    @Field("locale")
    private String locale;

    @Field("preferences")
    private String preferences;

    @Field("updatedAt")
    private LocalDateTime updatedAt;

    @Field("mental")
    @Min(value = 0, message = "Mental must be at least 0")
    @Max(value = 10, message = "Mental must be at most 10")
    private Integer mental;

    @Field("sleep")
    @Min(value = 0, message = "Sleep must be at least 0")
    @Max(value = 10, message = "Sleep must be at most 10")
    private Integer sleep;

    @Field("stress")
    @Min(value = 0, message = "Stress must be at least 0")
    @Max(value = 10, message = "Stress must be at most 10")
    private Integer stress;

    @Field("meditation")
    @Min(value = 0, message = "Meditation must be at least 0")
    @Max(value = 10, message = "Meditation must be at most 10")
    private Integer meditation;

    @Field("City")
    private String city;
}
