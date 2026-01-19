package com.unizg.fer.user;

import com.unizg.fer.config.ResourceNotFoundException;
import com.unizg.fer.config.UserAlreadyExistsException;
import com.unizg.fer.security.rbac.Role;
import com.unizg.fer.security.rbac.StringToRoleConverter;
import com.unizg.fer.security.rbac.UserRole;
import com.unizg.fer.security.rbac.UserRoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.module.ModuleDescriptor.Builder;
import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * Service class for managing user-related operations.
 */
@Service
public class UserService {

    private static final String RESOURCE_NOT_FOUND = "user not found";
    private static final String ID_CANNOT_BE_NULL = "id cannot be null";
    private static final String NO_USER = "No user found with the email address : ";
    private static final String USER_EXIST = "A user with this email address already exists.";

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserRoleRepository userRolesRepo;

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user to retrieve
     * @return the user with the specified email address
     * @throws ResourceNotFoundException if no user is found with the given email
     */
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(NO_USER + email));
    }

    @Autowired
    private StringToRoleConverter roleConverter;

    /**
     * Creates a new user with the provided details.
     *
     * @param email     the email address of the new user
     * @param firstName the first name of the new user
     * @param lastName  the last name of the new user
     * @param roles     the roles assigned to the new user
     * @param city      the city of the new user
     * @return the created user
     * @throws UserAlreadyExistsException if a user with the given email already
     *                                    exists
     */
    public User createUser(String email, String firstName, String lastName, String role, String city) {
        if (userRepo.existsByEmail(email)) {
            throw new UserAlreadyExistsException(USER_EXIST);
        }
        var user = User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .city(city)
                .build();

        var savedUser = userRepo.save(user);

        // create role for user
        Role roleEnum = roleConverter.convert(role);

        var userRole = UserRole.builder()
                .userId(user.getId())
                .role(roleEnum)
                .build();
        userRolesRepo.save(userRole);

        return savedUser;
    }

    public User getInfoById(String id) {
        if (id == null) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL);
        }
        return userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND));
    }

    /**
     * Updates an existing user's details.
     *
     * @param email       the email address of the user to update
     * @param userDetails the updated user details
     * @return the updated user
     * @throws ResourceNotFoundException if no user is found with the given email
     */
    public User updateUser(String email, User userDetails) {
        User existingUser = getUserByEmail(email);

        if (userDetails.getFirstName() != null)
            existingUser.setFirstName(userDetails.getFirstName());
        if (userDetails.getLastName() != null)
            existingUser.setLastName(userDetails.getLastName());
        if (userDetails.getLocale() != null)
            existingUser.setLocale(userDetails.getLocale());
        if (userDetails.getPreferences() != null)
            existingUser.setPreferences(userDetails.getPreferences());
        if (userDetails.getIsActive() != null)
            existingUser.setIsActive(userDetails.getIsActive());

        if (userDetails.getMental() != null)
            existingUser.setMental(userDetails.getMental());
        if (userDetails.getSleep() != null)
            existingUser.setSleep(userDetails.getSleep());
        if (userDetails.getStress() != null)
            existingUser.setStress(userDetails.getStress());
        if (userDetails.getMeditation() != null)
            existingUser.setMeditation(userDetails.getMeditation());
        if (userDetails.getCity() != null)
            existingUser.setCity(userDetails.getCity());
        existingUser.setUpdatedAt(LocalDateTime.now());

        return userRepo.save(existingUser);
    }

    /**
     * Deletes a user by their email address.
     *
     * @param email the email address of the user to delete
     * @throws ResourceNotFoundException if no user is found with the given email
     */
    public void deleteUser(String email) {
        User userToDelete = getUserByEmail(email);
        userRepo.delete(userToDelete);
    }

    public User findByemail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND));
    }

    /***
     * Must be annoted with @Transactionnal to keep conn open with mongo db
     * 
     * @return a stream with on all the users
     */
    @Transactional(readOnly = true)
    public Stream<User> findAll() {
        return userRepo.streamAll();
    }
}