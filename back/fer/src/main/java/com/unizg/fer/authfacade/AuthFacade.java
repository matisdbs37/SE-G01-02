package com.unizg.fer.authfacade;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unizg.fer.config.NotAuthticatedException;

/**
 * @author Martin NERON bean that implement
 * @see{AuthticationFacadeInterface.java} Use this bean in servicies when you
 * cannot retrieve infos from endpoints
 */
@Component
public class AuthFacade implements AuthenticationFacadeInterface {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    /**
     * This method allows us to extract the Auth user from the
     * securityContextHolder and create a JsonNode from it
     *
     * @return a JsonNode made of the currently log user
     */
    public JsonNode getAuthentication() {
        //Retrieve auth from securityContextHolder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //Check if the user is authenticated
        if (auth == null || !auth.isAuthenticated()) {
            throw new NotAuthticatedException("User is not authenticated");
        }

        //create an HashMap to store the datas
        Map<String, Object> authData = new HashMap<>();

        authData.put("authenticated", auth.isAuthenticated());
        authData.put("authorities", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        if (auth.getPrincipal() instanceof OAuth2User oauth2User) {
            authData.put("attributes", oauth2User.getAttributes());
        }

        return objectMapper.valueToTree(authData);
    }
}
