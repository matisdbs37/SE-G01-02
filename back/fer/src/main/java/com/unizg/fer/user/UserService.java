package com.unizg.fer.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unizg.fer.config.ResourceNotFoundException;

@Service
public class UserService {

    private static final String RESOURCE_NOT_FOUND = "user not found";
    private static final String ID_CANNOT_BE_NULL = "id cannot be null";

    @Autowired
    public UserRepository repo;

    public User getInfoByEmail(String email) {
        return repo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND));
    }

    public User getInfoById(String id) {
        if (id == null) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL);
        }

        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND));
    }
}
