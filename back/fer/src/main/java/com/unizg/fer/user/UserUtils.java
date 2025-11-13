package com.unizg.fer.user;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * @author Martin NERON Utiliraty class for user
 */
public class UserUtils {

    /**
     * Extract frist_name and family_name from google accont to match the user
     * POJO and entity model
     *
     * @return first_name and last_name if exists and a first_name equal to the
     * name field and an empty last name
     */
    public static ArrayList<String> GoogleOauth2NameRetriver(@AuthenticationPrincipal OAuth2User principal) {
        String firstName = null;
        String lastName = null;
        String fullName = principal.getAttribute("name");
        var result = new ArrayList<String>();

        //check if the principal have thoses fields
        if (principal.getAttribute("given_name") != null && principal.getAttribute("family_name") != null) {
            firstName = principal.getAttribute("given_name");
            lastName = principal.getAttribute("family_name");

        } else {
            //if not then full name = first name and family name = "" 
            firstName = fullName;
            lastName = " ";
        }

        result.add(firstName);
        result.add(lastName);
        return result;
    }
}
