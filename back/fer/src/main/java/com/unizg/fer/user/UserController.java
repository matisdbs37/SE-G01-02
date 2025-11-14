package com.unizg.fer.user;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.unizg.fer.authfacade.AuthFacade;
import com.unizg.fer.config.NotAuthticatedException;



/**
 * @author Martin NERON test user Controller
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final UserUtils utils = new UserUtils();

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    //Name of the email attribute in principal
    private static final String EMAIL_ATTRIBUTE = "email";

    //Name of the name attribute in principal
    private static final String NAME_ATTRIBUTE = "name";

    @Autowired
    AuthFacade auth;

    @Autowired
    UserRepository userRepository;

    // Endpoint to get current user info from OAuth2 principal 
    // First scoring cycle does not requier the Mail and password registration : TODO later
    /**
     * Log the user with GoogleOauth2 Create the user in the db if not exists
     *
     * @return ResponseEntity<Oauth2User>
     * @throws NotAuthticatedException
     */
    @GetMapping("/me")
    public ResponseEntity<JsonNode> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) throws NotAuthticatedException {
        // Check if principal is null and throw NotAuthticatedException if so
        if (principal == null) {
            throw new NotAuthticatedException("User is not authenticated");
        }

        System.out.println("=== AuthFacade Interface ===");
        System.out.println(auth.getAuthentication().toPrettyString());

        //check if account exists in db
        if (userRepository.findByEmail(principal.getAttribute(EMAIL_ATTRIBUTE)).isEmpty()) {
            //TODO : match more rigorously the actual user entity 
            var names = UserUtils.GoogleOauth2NameRetriver(principal);
            User newUser;
            newUser = User.builder()
                    .email(principal.getAttribute(EMAIL_ATTRIBUTE))
                    .firstName(names.get(0))
                    .lastName(names.get(1))
                    .roles(List.of("user"))
                    .createdAt(LocalDate.now())
                    .build();
            try {
                userRepository.save(newUser);
                LOGGER.info("new user register in the database");
            } catch (Exception e) {
                throw e;
            }
        }
        System.out.print("principal" + principal);
        return ResponseEntity.ok(auth.getAuthentication());
    }

}
