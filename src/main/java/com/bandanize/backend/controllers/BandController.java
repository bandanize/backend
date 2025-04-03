package com.bandanize.backend.controllers;

import com.bandanize.backend.exceptions.ErrorResponse;
import com.bandanize.backend.exceptions.ResourceNotFoundException;
import com.bandanize.backend.models.BandModel;
import com.bandanize.backend.models.UserModel;
import com.bandanize.backend.models.SongModel;
import com.bandanize.backend.repositories.BandRepository;
import com.bandanize.backend.repositories.UserRepository;
import com.bandanize.backend.repositories.SongRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bands")
public class BandController {

    @Autowired
    private BandRepository bandRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongRepository songRepository;

    // Add a user to a band and vice versa
    @PutMapping("/{bandId}/add-user/{userId}")
    public ResponseEntity<String> addUserToBand(@PathVariable Long bandId, @PathVariable Long userId) {
        // Find the band by ID
        BandModel band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + bandId));

        // Find the user by ID
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Find the user in the band
        if (!band.getUserIds().contains(userId.toString())) {
            band.getUserIds().add(userId.toString());
            bandRepository.save(band);
        }

        // Find the band in the user
        if (!user.getBandIds().contains(bandId.toString())) {
            user.getBandIds().add(bandId.toString());
            userRepository.save(user);
        }

        return ResponseEntity.ok("User added to band and band added to user successfully.");
    }

    // Get all bands
    @GetMapping
    public List<BandModel> getAllBands() {
        return bandRepository.findAll();
    }

    // Get a band by ID
    @GetMapping("/{id}")
    public BandModel getBandById(@PathVariable Long id) {
        return bandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + id));
    }

    // Create a new band
    @PostMapping
    public BandModel createBand(@RequestBody BandModel band) {
        return bandRepository.save(band);
    }

    // Update an existing band
    @PutMapping("/{id}")
    public BandModel updateBand(@PathVariable Long id, @RequestBody BandModel bandDetails) {
        BandModel band = bandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + id));

        band.setName(bandDetails.getName());
        band.setPhoto(bandDetails.getPhoto());
        band.setDescription(bandDetails.getDescription());
        band.setGenre(bandDetails.getGenre());
        band.setCity(bandDetails.getCity());
        band.setComments(bandDetails.getComments());
        band.setRrss(bandDetails.getRrss());
        band.setSongIds(bandDetails.getSongIds());
        band.setUserIds(bandDetails.getUserIds());

        return bandRepository.save(band);
    }

    // Delete a band
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBand(@PathVariable Long id) {
        BandModel band = bandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + id));
        bandRepository.delete(band);
        return ResponseEntity.noContent().build();
    }

    // Exception handler for ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Resource not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}