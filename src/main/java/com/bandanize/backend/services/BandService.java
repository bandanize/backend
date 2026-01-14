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
     * Converts a BandModel entity to a BandDTO.
     *
     * @param band The BandModel entity.
     * @return The corresponding BandDTO.
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
