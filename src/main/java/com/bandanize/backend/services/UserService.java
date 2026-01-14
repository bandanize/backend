package com.bandanize.backend.services;

import com.bandanize.backend.dtos.UserDTO;
import com.bandanize.backend.exceptions.ResourceNotFoundException;
import com.bandanize.backend.models.BandModel;
import com.bandanize.backend.models.UserModel;
import com.bandanize.backend.repositories.BandRepository;
import com.bandanize.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing User-related operations.
 * Handles business logic for user creation, retrieval, updates, and deletion.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BandRepository bandRepository;

    @Autowired
    public UserService(UserRepository userRepository, BandRepository bandRepository) {
        this.userRepository = userRepository;
        this.bandRepository = bandRepository;
    }

    /**
     * Retrieves all users.
     *
     * @return List of UserDTOs representing all users.
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new user.
     *
     * @param user The user entity to save.
     * @return The created UserDTO.
     */
    public UserDTO createUser(UserModel user) {
        UserModel savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username to search for.
     * @return The found UserDTO.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public UserDTO getUserByUsername(String username) {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return convertToDTO(user);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID to search for.
     * @return The found UserDTO.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public UserDTO getUserById(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDTO(user);
    }

    /**
     * Updates an existing user's details.
     *
     * @param id          The ID of the user to update.
     * @param userDetails The new user details.
     * @return The updated UserDTO.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public UserDTO updateUser(Long id, UserModel userDetails) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update only the fields provided in the request body
        if (userDetails.getName() != null)
            user.setName(userDetails.getName());
        if (userDetails.getEmail() != null)
            user.setEmail(userDetails.getEmail());
        if (userDetails.getUsername() != null)
            user.setUsername(userDetails.getUsername());
        if (userDetails.getCity() != null)
            user.setCity(userDetails.getCity());
        if (userDetails.getPhoto() != null)
            user.setPhoto(userDetails.getPhoto());
        if (userDetails.getRrss() != null && !userDetails.getRrss().isEmpty()) {
            user.setRrss(userDetails.getRrss());
        }

        UserModel updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    /**
     * Deletes a user by their ID.
     * Removes the user from all associated bands before deletion.
     *
     * @param id The ID of the user to delete.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public void deleteUser(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Remove the user from all associated bands
        for (BandModel band : user.getBands()) {
            band.getUsers().remove(user);
            bandRepository.save(band);
        }

        // Delete the user
        userRepository.delete(user);
    }

    /**
     * Searches for users by email or username containing the query string.
     *
     * @param query The search query.
     * @return List of matching UserDTOs.
     */
    public List<UserDTO> searchUsers(String query) {
        // Search by email first, could extend to username too
        List<UserModel> users = userRepository.findByEmailContaining(query);
        if (users.isEmpty()) {
            users = userRepository.findByUsernameContaining(query);
        }

        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a UserModel entity to a UserDTO.
     *
     * @param user The UserModel entity.
     * @return The corresponding UserDTO.
     */
    private UserDTO convertToDTO(UserModel user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setCity(user.getCity());
        userDTO.setRrss(user.getRrss());
        userDTO.setPhoto(user.getPhoto());
        userDTO.setBandIds(user.getBands().stream()
                .map(BandModel::getId)
                .collect(Collectors.toList()));
        return userDTO;
    }
}
