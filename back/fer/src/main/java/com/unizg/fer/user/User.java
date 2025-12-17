package com.unizg.fer.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.mongodb.lang.NonNull;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "Users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    //TODO: updated user with required fields
    
    @Id
    @NonNull
    private String id;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Field("first_name")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Field("last_name")
    @NotBlank(message = "Last name is required")
    private String lastName;

    private String password;

    @NotEmpty(message = "At least one role is required")
    private List<String> roles;

    @Builder.Default
    private Status status = Status.ACTIVE;

    private String locale;

    private String timeZone;

    private Map<String, Object> preferences;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
