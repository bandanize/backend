package com.bandanize.backend.controllers;

import com.bandanize.backend.dtos.BandDTO;
import com.bandanize.backend.exceptions.ErrorResponse;
import com.bandanize.backend.exceptions.ResourceNotFoundException;
import com.bandanize.backend.models.BandModel;
import com.bandanize.backend.models.UserModel;
import com.bandanize.backend.repositories.BandRepository;
import com.bandanize.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bands")
public class BandController {

    @Autowired
    private BandRepository bandRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all bands
    @GetMapping
    public List<BandDTO> getAllBands() {
        return bandRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get a band by ID
    @GetMapping("/{id}")
    public ResponseEntity<BandDTO> getBandById(@PathVariable Long id) {
        BandModel band = bandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + id));
        return ResponseEntity.ok(convertToDTO(band));
    }

    @GetMapping("/my-bands")
    public ResponseEntity<List<BandDTO>> getMyBands(@AuthenticationPrincipal UserDetails userDetails) {
        UserModel user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + userDetails.getUsername()));
    
        List<BandDTO> bands = user.getBands().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    
        return ResponseEntity.ok(bands);
    }

    // Update a band
    @PutMapping("/{id}")
    public ResponseEntity<BandDTO> updateBand(@PathVariable Long id, @RequestBody BandModel bandDetails) {
        BandModel band = bandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + id));

        if (bandDetails.getName() != null) band.setName(bandDetails.getName());
        if (bandDetails.getPhoto() != null) band.setPhoto(bandDetails.getPhoto());
        if (bandDetails.getDescription() != null) band.setDescription(bandDetails.getDescription());
        if (bandDetails.getGenre() != null) band.setGenre(bandDetails.getGenre());
        if (bandDetails.getCity() != null) band.setCity(bandDetails.getCity());
        if (bandDetails.getRrss() != null && !bandDetails.getRrss().isEmpty()) band.setRrss(bandDetails.getRrss());

        BandModel updatedBand = bandRepository.save(band);
        return ResponseEntity.ok(convertToDTO(updatedBand));
    }

    // Create a new band
    @PostMapping
    public BandDTO createBand(@RequestBody BandModel band) {
        BandModel savedBand = bandRepository.save(band);
        return convertToDTO(savedBand);
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<BandDTO> createBandWithUser(@PathVariable Long userId, @RequestBody BandModel bandDetails) {
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

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedBand));
    }

    // Convert BandModel to BandDTO
    private BandDTO convertToDTO(BandModel band) {
        BandDTO bandDTO = new BandDTO();
        bandDTO.setId(band.getId());
        bandDTO.setName(band.getName());
        bandDTO.setPhoto(band.getPhoto());
        bandDTO.setDescription(band.getDescription());
        bandDTO.setGenre(band.getGenre());
        bandDTO.setCity(band.getCity());
        bandDTO.setRrss(band.getRrss());
        bandDTO.setUserIds(band.getUsers().stream().map(UserModel::getId).collect(Collectors.toList()));
        return bandDTO;
    }

    // Exception handler for ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Resource not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}