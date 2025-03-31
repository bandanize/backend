package com.bandanize.backend.controllers;

import com.bandanize.backend.exceptions.ErrorResponse;
import com.bandanize.backend.exceptions.ResourceNotFoundException;
import com.bandanize.backend.models.SongModel;
import com.bandanize.backend.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    @Autowired
    private SongRepository songRepository;

    // Get all songs
    @GetMapping
    public List<SongModel> getAllSongs() {
        return songRepository.findAll();
    }

    // Get a song by ID
    @GetMapping("/{id}")
    public SongModel getSongById(@PathVariable Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found with id: " + id));
    }

    // Create a new song
    @PostMapping
    public SongModel createSong(@RequestBody SongModel song) {
        return songRepository.save(song);
    }

    // Update an existing song
    @PutMapping("/{id}")
    public SongModel updateSong(@PathVariable Long id, @RequestBody SongModel songDetails) {
        SongModel song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found with id: " + id));

        song.setTitle(songDetails.getTitle());
        song.setBand(songDetails.getBand());
        song.setBpm(songDetails.getBpm());
        song.setKey(songDetails.getKey());
        song.setMedia(songDetails.getMedia());
        song.setPlaylistId(songDetails.getPlaylistId());
        song.setTabIds(songDetails.getTabIds());

        return songRepository.save(song);
    }

    // Delete a song
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        SongModel song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found with id: " + id));
        songRepository.delete(song);
        return ResponseEntity.noContent().build();
    }

    // Exception handler for ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Resource not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}