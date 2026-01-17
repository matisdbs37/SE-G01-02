package com.unizg.fer.security.rbac;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRoleRepository extends MongoRepository<UserRole, String> {
    Optional<UserRole> findByUserId(String userId);

    void deleteByUserId(String userId);

}
