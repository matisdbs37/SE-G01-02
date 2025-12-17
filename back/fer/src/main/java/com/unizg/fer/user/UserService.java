package com.unizg.fer.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unizg.fer.config.ResourceNotFoundException;

@Service
public class UserService {

    private static final String RESOURCE_NOT_FOUND = "user not found";

    @Autowired
    public UserRepository repo;

    public User getInfo(String email){
        System.out.println(repo.findAll());
        return repo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND));
    }
}
