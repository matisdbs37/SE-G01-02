package com.unizg.fer.security.rbac;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.mongodb.lang.NonNull;
import com.unizg.fer.config.ResourceNotFoundException;
import com.unizg.fer.user.UserRepository;
import com.unizg.fer.user.UserService;

/**
 * Using Once per request filter to retrieve the user by email and check is role
 * SINCE OUR USER have one role either ROLE_ADMIN or ROLE_USER we are converting
 * to only one grantedAuthority and not a collection
 * this does not support many roles for 1 user
 */
public class JwtRoleChecker implements Converter<Jwt, AbstractAuthenticationToken> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserService userService;

    private final static String EMAIL_FIELD = "email";

    private final static String ROLES_NOT_FOUND = "roles not found for userID : %s, and email : %s";

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRoleChecker.class);

    @Override
    @NonNull
    public AbstractAuthenticationToken convert(Jwt token) {
        // get email in claims
        String email = token.getClaim(EMAIL_FIELD);

        // retrieve user from email
        var user = userService.findOrCreateUserByEmail(email);

        // get the user's role
        GrantedAuthority authority = userRoleRepository.findByUserId(user.getId())
                .map(userRole -> (GrantedAuthority) new SimpleGrantedAuthority(userRole.getRole().getValue()))
                .orElseThrow(() -> {
                    LOGGER.info(String.format(ROLES_NOT_FOUND, user.getId(), user.getEmail()));
                    return new ResourceNotFoundException(email);
                });
        return new JwtAuthenticationToken(token, List.of(authority));
    }

}
