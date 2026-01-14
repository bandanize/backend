package com.bandanize.backend.controllers;

import com.bandanize.backend.dtos.BandDTO;
import com.bandanize.backend.exceptions.ErrorResponse;
import com.bandanize.backend.exceptions.ResourceNotFoundException;
import com.bandanize.backend.models.BandModel;
import com.bandanize.backend.services.BandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing Bands.
 * Exposes endpoints for creating, retrieving, and updating bands.
 */
@RestController
@RequestMapping("/api/bands")
public class BandController {

    private final BandService bandService;

    @Autowired
    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    /**
     * Retrieves all bands.
     *
     * @return List of BandDTOs.
     */
    @GetMapping
    public List<BandDTO> getAllBands() {
        return bandService.getAllBands();
    }

    /**
     * Retrieves a band by its ID.
     *
     * @param id The ID of the band.
     * @return ResponseEntity with the BandDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BandDTO> getBandById(@PathVariable Long id) {
        BandDTO bandDTO = bandService.getBandById(id);
        return ResponseEntity.ok(bandDTO);
    }

    /**
     * Retrieves the bands associated with the authenticated user.
     *
     * @param userDetails The authenticated user details.
     * @return ResponseEntity with the list of BandDTOs.
     */
    @GetMapping("/my-bands")
    public ResponseEntity<List<BandDTO>> getMyBands(@AuthenticationPrincipal UserDetails userDetails) {
        List<BandDTO> bands = bandService.getBandsByUsername(userDetails.getUsername());
        return ResponseEntity.ok(bands);
    }

    /**
     * Updates an existing band.
     *
     * @param id          The ID of the band to update.
     * @param bandDetails The updated details.
     * @return ResponseEntity with the updated BandDTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BandDTO> updateBand(@PathVariable Long id, @RequestBody BandModel bandDetails) {
        BandDTO updatedBand = bandService.updateBand(id, bandDetails);
        return ResponseEntity.ok(updatedBand);
    }

    /**
     * Creates a new band.
     *
     * @param band The band to create.
     * @return The created BandDTO.
     */
    @PostMapping
    public BandDTO createBand(@RequestBody BandModel band) {
        return bandService.createBand(band);
    }

    /**
     * Creates a new band associated with a specific user.
     *
     * @param userId      The ID of the user.
     * @param bandDetails The band details.
     * @return ResponseEntity with the created BandDTO.
     */
    @PostMapping("/create/{userId}")
    public ResponseEntity<BandDTO> createBandWithUser(@PathVariable Long userId, @RequestBody BandModel bandDetails) {
        BandDTO createdBand = bandService.createBandWithUser(userId, bandDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBand);
    }

    /**
     * Adds a member to the band.
     *
     * @param bandId The ID of the band.
     * @param body   Map containing the email of the user to add.
     * @return ResponseEntity with the updated BandDTO.
     */
    @PostMapping("/{bandId}/members")
    public ResponseEntity<BandDTO> addMember(@PathVariable Long bandId, @RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        BandDTO updatedBand = bandService.addMember(bandId, email);
        return ResponseEntity.ok(updatedBand);
    }

    /**
     * Exception handler for ResourceNotFoundException.
     *
     * @param ex The exception.
     * @return ResponseEntity with error details.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Resource not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}