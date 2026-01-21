package com.unizg.fer.security.rbac;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.unizg.fer.config.ResourceNotFoundException;
import com.unizg.fer.user.User;
import com.unizg.fer.user.UserRepository;

@Service
public class UserRoleService {

    private static final String ROLE_NOT_FOUND = "role not found for user : %s";
    private static final String USER_NOT_FOUND = "user not found for email : %s";

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    public GrantedAuthority getUserAuthority(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND, email)));
        return userRoleRepository.findByUserId(user.getId())
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getValue()))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ROLE_NOT_FOUND, user.getEmail())));
    }
}
