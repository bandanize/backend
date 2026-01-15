package com.bandanize.backend.controllers;

import com.bandanize.backend.dtos.ChangePasswordDTO;
import com.bandanize.backend.exceptions.ResourceNotFoundException;
import com.bandanize.backend.models.UserModel;
import com.bandanize.backend.repositories.UserRepository;
import com.bandanize.backend.services.JwtService;
import com.bandanize.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling authentication (Login/Register).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
            PasswordEncoder passwordEncoder, UserRepository userRepository, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param authRequest The login request containing username and password.
     * @return ResponseEntity with the JWT token or error message.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            final UserModel user = userRepository.findByUsername(authRequest.getUsername())
                    .or(() -> userRepository.findByEmail(authRequest.getUsername()))
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            String token = jwtService.generateToken(user.getUsername());

            return ResponseEntity
                    .ok(new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getName()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    /**
     * Registers a new user.
     *
     * @param user The user details to register.
     * @return ResponseEntity with success or error message.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserModel user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        user.setHashedPassword(passwordEncoder.encode(user.getHashedPassword()));
        user.setDisabled(false); // Enable the user by default
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    /**
     * Changes the password of the authenticated user.
     *
     * @param request        The change password request.
     * @param authentication The authentication object.
     * @return ResponseEntity with success or error message.
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO request,
            org.springframework.security.core.Authentication authentication) {
        try {

            // Better way: Get username from authentication -> Get User -> Get ID
            String username = authentication.getName();
            com.bandanize.backend.dtos.UserDTO user = userService.getUserByUsername(username);

            userService.changePassword(user.getId(), request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

/**
 * DTO for authentication request.
 */
class AuthRequest {
    private String username;
    private String password;

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

/**
 * DTO for authentication response.
 */
class AuthResponse {
    private String token;
    private Long id;
    private String username;
    private String email;
    private String name;

    public AuthResponse(String token, Long id, String username, String email, String name) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}