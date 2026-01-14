package com.bandanize.backend.services;

import com.bandanize.backend.dtos.BandDTO;
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
 * Service class for managing Band-related operations.
 * Handles creation, retrieval, updates, and association of bands with users.
 */
@Service
public class BandService {

    private final BandRepository bandRepository;
    private final UserRepository userRepository;

    @Autowired
    public BandService(BandRepository bandRepository, UserRepository userRepository) {
        this.bandRepository = bandRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all bands.
     *
     * @return List of BandDTOs.
     */
    public List<BandDTO> getAllBands() {
        return bandRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a band by its ID.
     *
     * @param id The ID of the band.
     * @return The found BandDTO.
     * @throws ResourceNotFoundException if the band is not found.
     */
    public BandDTO getBandById(Long id) {
        BandModel band = bandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + id));
        return convertToDTO(band);
    }

    /**
     * Creates a new band.
     *
     * @param band The band entity to save.
     * @return The created BandDTO.
     */
    public BandDTO createBand(BandModel band) {
        BandModel savedBand = bandRepository.save(band);
        return convertToDTO(savedBand);
    }

    /**
     * Creates a new band and associates it with a user.
     *
     * @param userId      The ID of the user creating the band.
     * @param bandDetails The details of the band to create.
     * @return The created BandDTO.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public BandDTO createBandWithUser(Long userId, BandModel bandDetails) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        BandModel band = new BandModel();
        band.setName(bandDetails.getName());
        band.setPhoto(bandDetails.getPhoto());
        band.setDescription(bandDetails.getDescription());
        band.setGenre(bandDetails.getGenre());
        band.setCity(bandDetails.getCity());
        band.setRrss(bandDetails.getRrss());
        band.getUsers().add(user);

        BandModel savedBand = bandRepository.save(band);
        user.getBands().add(savedBand);
        userRepository.save(user);

        return convertToDTO(savedBand);
    }

    /**
     * Updates an existing band.
     *
     * @param id          The ID of the band to update.
     * @param bandDetails The new band details.
     * @return The updated BandDTO.
     * @throws ResourceNotFoundException if the band is not found.
     */
    public BandDTO updateBand(Long id, BandModel bandDetails) {
        BandModel band = bandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + id));

        if (bandDetails.getName() != null)
            band.setName(bandDetails.getName());
        if (bandDetails.getPhoto() != null)
            band.setPhoto(bandDetails.getPhoto());
        if (bandDetails.getDescription() != null)
            band.setDescription(bandDetails.getDescription());
        if (bandDetails.getGenre() != null)
            band.setGenre(bandDetails.getGenre());
        if (bandDetails.getCity() != null)
            band.setCity(bandDetails.getCity());
        if (bandDetails.getRrss() != null && !bandDetails.getRrss().isEmpty()) {
            band.setRrss(bandDetails.getRrss());
        }

        BandModel updatedBand = bandRepository.save(band);
        return convertToDTO(updatedBand);
    }

    /**
     * Retrieves all bands associated with a specific username.
     *
     * @param username The username of the user.
     * @return List of BandDTOs associated with the user.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public List<BandDTO> getBandsByUsername(String username) {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return user.getBands().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Adds a user to a band by email.
     *
     * @param bandId The ID of the band.
     * @param email  The email of the user to add.
     * @return The updated BandDTO.
     * @throws ResourceNotFoundException if the band or user is not found.
     */
    public BandDTO addMember(Long bandId, String email) {
        BandModel band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + bandId));

        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Check if user is already a member
        if (band.getUsers().contains(user)) {
            return convertToDTO(band);
        }

        band.getUsers().add(user);
        BandModel savedBand = bandRepository.save(band);

        // Also update user side if needed (depending on cascade/fetch settings, but
        // safe to save band usually)
        return convertToDTO(savedBand);
    }

    /**
     * Adds a chat message to a band.
     *
     * @param bandId  The ID of the band.
     * @param request The chat message request containing userId and message.
     * @return The saved ChatMessageModel.
     * @throws ResourceNotFoundException if band or user is not found.
     */
    public com.bandanize.backend.models.ChatMessageModel addChatMessage(Long bandId,
            com.bandanize.backend.dtos.ChatMessageRequestDTO request) {
        BandModel band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + bandId));

        UserModel user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        com.bandanize.backend.models.ChatMessageModel message = new com.bandanize.backend.models.ChatMessageModel();
        message.setBand(band);
        message.setSender(user);
        message.setMessage(request.getMessage());
        message.setTimestamp(java.time.LocalDateTime.now());

        band.getChatMessages().add(message);
        bandRepository.save(band);

        // Return the last added message (which is the one we just added)
        // Since it's a list, getting the last element is safe-ish for now.
        // A better approach would be to save message via ChatMessageRepository if we
        // had one.
        // But cascading save on Band works. The ID will be generated upon save.
        return band.getChatMessages().get(band.getChatMessages().size() - 1);
    }

    /**
     * Helper method to convert BandModel to BandDTO.
     *
     * @param band The BandModel.
     * @return The BandDTO.
     */
    private BandDTO convertToDTO(BandModel band) {
        BandDTO bandDTO = new BandDTO();
        bandDTO.setId(band.getId());
        bandDTO.setName(band.getName());
        bandDTO.setPhoto(band.getPhoto());
        bandDTO.setDescription(band.getDescription());
        bandDTO.setGenre(band.getGenre());
        bandDTO.setCity(band.getCity());
        bandDTO.setRrss(band.getRrss());
        bandDTO.setMembers(band.getUsers().stream()
                .map(user -> new com.bandanize.backend.dtos.UserSummaryDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getName(),
                        user.getEmail()))
                .collect(Collectors.toList()));
        bandDTO.setSongLists(band.getSongLists());
        bandDTO.setChatMessages(band.getChatMessages());
        return bandDTO;
    }
}
