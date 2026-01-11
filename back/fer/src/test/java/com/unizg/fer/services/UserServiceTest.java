package com.unizg.fer.services;

import com.unizg.fer.config.ResourceNotFoundException;
import com.unizg.fer.config.UserAlreadyExistsException;
import com.unizg.fer.user.User;
import com.unizg.fer.user.UserRepository;
import com.unizg.fer.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the `UserService` class.
 * Uses Mockito to mock dependencies and verify interactions.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserService userService;

    /**
     * Tests that `getUserByEmail` returns the user when the email is found.
     */
    @Test
    void getUserByEmail_shouldReturnUser_whenFound() {
        String email = "test@example.com";
        User mockUser = User.builder().email(email).build();

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(mockUser));

        User result = userService.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    /**
     * Tests that `getUserByEmail` throws `ResourceNotFoundException` when the email is not found.
     */
    @Test
    void getUserByEmail_shouldThrow_whenNotFound() {
        String email = "unknown@example.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail(email));
    }

    /**
     * Tests that `createUser` saves the user when the email does not already exist.
     */
    @Test
    void createUser_shouldSaveUser_whenEmailDoesNotExist() {
        String email = "new@example.com";
        when(userRepo.existsByEmail(email)).thenReturn(false);

        when(userRepo.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User result = userService.createUser(email, "John", "Doe", "USER", "Paris");

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals("John", result.getFirstName());
        assertTrue(result.getIsActive());
        assertNotNull(result.getCreatedAt());

        verify(userRepo).save(any(User.class));
    }

    /**
     * Tests that `createUser` throws `UserAlreadyExistsException` when the email already exists.
     */
    @Test
    void createUser_shouldThrow_whenEmailExists() {
        String email = "existing@example.com";
        when(userRepo.existsByEmail(email)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.createUser(email, "John", "Doe", "USER", "Paris")
        );

        verify(userRepo, never()).save(any());
    }

    /**
     * Tests that `getInfoById` returns the user when the ID is found.
     */
    @Test
    void getInfoById_shouldReturnUser_whenFound() {
        String id = "123";
        User mockUser = User.builder().id(id).build();
        when(userRepo.findById(id)).thenReturn(Optional.of(mockUser));

        User result = userService.getInfoById(id);

        assertEquals(id, result.getId());
    }

    /**
     * Tests that `getInfoById` throws `IllegalArgumentException` when the ID is null.
     */
    @Test
    void getInfoById_shouldThrow_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> userService.getInfoById(null));
    }

    /**
     * Tests that `getInfoById` throws `ResourceNotFoundException` when the ID is not found.
     */
    @Test
    void getInfoById_shouldThrow_whenNotFound() {
        String id = "unknown";
        when(userRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getInfoById(id));
    }

    /**
     * Tests that `updateUser` updates only the non-null fields of the user.
     */
    @Test
    void updateUser_shouldUpdateOnlyNonNullFields() {
        String email = "update@example.com";

        User existingUser = User.builder()
                .email(email)
                .firstName("OldName")
                .lastName("OldLast")
                .city("OldCity")
                .build();

        User updateDetails = User.builder()
                .firstName("NewName")
                .lastName(null)
                .build();

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(userRepo.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User result = userService.updateUser(email, updateDetails);

        assertEquals("NewName", result.getFirstName());
        assertEquals("OldLast", result.getLastName());
        assertEquals("OldCity", result.getCity());
        assertNotNull(result.getUpdatedAt());

        verify(userRepo).save(existingUser);
    }

    /**
     * Tests that `updateUser` throws `ResourceNotFoundException` when the user is not found.
     */
    @Test
    void updateUser_shouldThrow_whenUserNotFound() {
        String email = "unknown@example.com";
        User updateDetails = new User();

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(email, updateDetails));
    }

    /**
     * Tests that `deleteUser` deletes the user when the email is found.
     */
    @Test
    void deleteUser_shouldDelete_whenFound() {
        String email = "delete@example.com";
        User mockUser = User.builder().email(email).build();

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(mockUser));

        userService.deleteUser(email);

        verify(userRepo).delete(mockUser);
    }

    /**
     * Tests that `deleteUser` throws `ResourceNotFoundException` when the email is not found.
     */
    @Test
    void deleteUser_shouldThrow_whenNotFound() {
        String email = "unknown@example.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(email));

        verify(userRepo, never()).delete(any());
    }
}