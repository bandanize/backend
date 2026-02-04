package com.bandanize.backend.services;

import com.bandanize.backend.dtos.UserDTO;
import com.bandanize.backend.models.UserModel;
import com.bandanize.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserModel user;

    @BeforeEach
    void setUp() {
        user = new UserModel();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setName("Test User");
    }

    @Test
    void createUser_ReturnsUserDTO() {
        when(userRepository.save(any(UserModel.class))).thenReturn(user);

        UserDTO result = userService.createUser(user);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    @Test
    void getUserByUsername_Found_ReturnsUserDTO() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserById_Found_ReturnsUserDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}
