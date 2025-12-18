package com.unizg.fer.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing `User` entities in the MongoDB database.
 * Extends the `MongoRepository` interface to provide CRUD operations and custom query methods.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user to find
     * @return an `Optional` containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given email address.
     *
     * @param email the email address to check for existence
     * @return `true` if a user with the given email exists, `false` otherwise
     */
    boolean existsByEmail(String email);
}