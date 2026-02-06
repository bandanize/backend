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
     * Validates the current token and returns user details.
     *
     * @param authentication The authentication object.
     * @return ResponseEntity with the user details.
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(org.springframework.security.core.Authentication authentication) {
        String username = authentication.getName();
        UserModel user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseEntity
                .ok(new AuthResponse("VALID", user.getId(), user.getUsername(), user.getEmail(), user.getName()));
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
        } catch (org.springframework.security.authentication.DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is disabled. Please verify your email.");
        } catch (org.springframework.security.authentication.LockedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is locked.");
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
        user.setDisabled(true); // User is disabled until verification
        userRepository.save(user);

        // Generate verification token and send email
        String token = jwtService.generateVerificationToken(user.getUsername());
        // Simpler: Inject EmailService in AuthController or add method to UserService
        // Let's use UserService to encapsulate this logic properly
        userService.sendVerificationEmail(user, token);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully. Please check your email to verify your account.");
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            userService.verifyUser(token);
            return ResponseEntity.ok("Email verified successfully");
        } catch (io.jsonwebtoken.JwtException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            userService.processForgotPassword(request.getEmail());
            return ResponseEntity.ok("Password reset email sent");
        } catch (ResourceNotFoundException e) {
            // Return 404 for now as per simple impl.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Password reset successfully");
        } catch (io.jsonwebtoken.JwtException e) { // Catch JWT errors (expired, invalid)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

class ForgotPasswordRequest {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

class ResetPasswordRequest {
    private String token;
    private String newPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
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