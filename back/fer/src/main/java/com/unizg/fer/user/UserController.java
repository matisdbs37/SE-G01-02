package com.unizg.fer.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/user")
public class UserController {

    //TODO get user by ID because email in url is NOT secure;
    
    //TODO make a function that return the user id from an access token

    @Autowired
    public UserService service;

    @GetMapping("{email}")
    public ResponseEntity<User> getUserInfo(@PathVariable String email){
        var user = service.getInfo(email);
        return ResponseEntity.ok(user);

    }
}
