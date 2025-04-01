package com.bandanize.backend.controllers;

import com.bandanize.backend.models.UserModel;
import com.bandanize.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    private UserModel user;

    @BeforeEach
    void setUp() {
        user = new UserModel();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setFullName("Test User Full");
        user.setCity("Test City");
        user.setHashedPassword("hashedpassword");
        user.setDisabled(false);
        user.setPhoto("https://example.com/photo.png");
        user.setRrss(Arrays.asList("https://twitter.com/testuser", "https://facebook.com/testuser"));
        user.setBandIds(Arrays.asList("band1", "band2"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test User"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].fullName").value("Test User Full"))
                .andExpect(jsonPath("$[0].city").value("Test City"))
                .andExpect(jsonPath("$[0].disabled").value(false))
                .andExpect(jsonPath("$[0].photo").value("https://example.com/photo.png"))
                .andExpect(jsonPath("$[0].rrss[0]").value("https://twitter.com/testuser"))
                .andExpect(jsonPath("$[0].bandIds[0]").value("band1"));
    }

    @Test
    void testGetUserById() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.fullName").value("Test User Full"))
                .andExpect(jsonPath("$.city").value("Test City"))
                .andExpect(jsonPath("$.disabled").value(false))
                .andExpect(jsonPath("$.photo").value("https://example.com/photo.png"))
                .andExpect(jsonPath("$.rrss[0]").value("https://twitter.com/testuser"))
                .andExpect(jsonPath("$.bandIds[0]").value("band1"));
    }

    @Test
    void testCreateUser() throws Exception {
        when(userRepository.save(any(UserModel.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test User\", \"email\": \"test@example.com\", \"username\": \"testuser\", " +
                                "\"fullName\": \"Test User Full\", \"city\": \"Test City\", \"hashedPassword\": \"hashedpassword\", " +
                                "\"disabled\": false, \"photo\": \"https://example.com/photo.png\", " +
                                "\"rrss\": [\"https://twitter.com/testuser\", \"https://facebook.com/testuser\"], " +
                                "\"bandIds\": [\"band1\", \"band2\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.fullName").value("Test User Full"))
                .andExpect(jsonPath("$.city").value("Test City"))
                .andExpect(jsonPath("$.disabled").value(false))
                .andExpect(jsonPath("$.photo").value("https://example.com/photo.png"))
                .andExpect(jsonPath("$.rrss[0]").value("https://twitter.com/testuser"))
                .andExpect(jsonPath("$.bandIds[0]").value("band1"));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserModel.class))).thenReturn(user);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated User\", \"email\": \"updated@example.com\", \"username\": \"updateduser\", " +
                                "\"fullName\": \"Updated User Full\", \"city\": \"Updated City\", \"hashedPassword\": \"updatedpassword\", " +
                                "\"disabled\": true, \"photo\": \"https://example.com/updated-photo.png\", " +
                                "\"rrss\": [\"https://twitter.com/updateduser\"], \"bandIds\": [\"updatedband\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.fullName").value("Updated User Full"))
                .andExpect(jsonPath("$.city").value("Updated City"))
                .andExpect(jsonPath("$.disabled").value(true))
                .andExpect(jsonPath("$.photo").value("https://example.com/updated-photo.png"))
                .andExpect(jsonPath("$.rrss[0]").value("https://twitter.com/updateduser"))
                .andExpect(jsonPath("$.bandIds[0]").value("updatedband"));
    }

    @Test
    void testDeleteUser() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userRepository).delete(user);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}