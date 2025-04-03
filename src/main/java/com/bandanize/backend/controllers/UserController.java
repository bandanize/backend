package com.bandanize.backend.controllers;

import com.bandanize.backend.dtos.UserDTO;
import com.bandanize.backend.exceptions.ErrorResponse;
import com.bandanize.backend.exceptions.ResourceNotFoundException;
import com.bandanize.backend.models.BandModel;
import com.bandanize.backend.models.UserModel;
import com.bandanize.backend.repositories.UserRepository;
import com.bandanize.backend.repositories.BandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BandRepository bandRepository;

    // Get all users
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Create a new user
    @PostMapping
    public UserDTO createUser(@RequestBody UserModel user) {
        UserModel savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    // Get my user details
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return ResponseEntity.ok(convertToDTO(user));
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(convertToDTO(user));
    }

    // Update a user
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UserModel userDetails) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update only the fields provided in the request body
        if (userDetails.getName() != null) user.setName(userDetails.getName());
        if (userDetails.getEmail() != null) user.setEmail(userDetails.getEmail());
        if (userDetails.getUsername() != null) user.setUsername(userDetails.getUsername());
        if (userDetails.getCity() != null) user.setCity(userDetails.getCity());
        if (userDetails.getHashedPassword() != null) user.setHashedPassword(userDetails.getHashedPassword());
        if (userDetails.getPhoto() != null) user.setPhoto(userDetails.getPhoto());
        if (userDetails.getRrss() != null && !userDetails.getRrss().isEmpty()) user.setRrss(userDetails.getRrss());
        user.setDisabled(userDetails.isDisabled());

        UserModel updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    // Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Remove the user from all associated bands
        for (BandModel band : user.getBands()) {
            band.getUsers().remove(user);
            bandRepository.save(band);
        }

        // Delete the user
        userRepository.delete(user);

        return ResponseEntity.noContent().build();
    }

    // Convert UserModel to UserDTO
    private UserDTO convertToDTO(UserModel user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setCity(user.getCity());
        userDTO.setPhoto(user.getPhoto());
        userDTO.setBandIds(user.getBands().stream().map(BandModel::getId).collect(Collectors.toList()));
        return userDTO;
    }

    // Exception handler for ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Resource not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}