package com.bandanize.backend.controllers;

import com.bandanize.backend.exceptions.ErrorResponse;
import com.bandanize.backend.exceptions.ResourceNotFoundException;
import com.bandanize.backend.models.PlaylistModel;
import com.bandanize.backend.repositories.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistRepository playlistRepository;

    // Get all playlists
    @GetMapping
    public List<PlaylistModel> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    // Get a playlist by ID
    @GetMapping("/{id}")
    public PlaylistModel getPlaylistById(@PathVariable Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + id));
    }

    // Create a new playlist
    @PostMapping
    public PlaylistModel createPlaylist(@RequestBody PlaylistModel playlist) {
        return playlistRepository.save(playlist);
    }

    // Update an existing playlist
    @PutMapping("/{id}")
    public PlaylistModel updatePlaylist(@PathVariable Long id, @RequestBody PlaylistModel playlistDetails) {
        PlaylistModel playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + id));

        playlist.setTitle(playlistDetails.getTitle());
        playlist.setDescription(playlistDetails.getDescription());
        playlist.setPhoto(playlistDetails.getPhoto());
        playlist.setBandId(playlistDetails.getBandId());
        playlist.setSongIds(playlistDetails.getSongIds());

        return playlistRepository.save(playlist);
    }

    // Delete a playlist
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        PlaylistModel playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + id));
        playlistRepository.delete(playlist);
        return ResponseEntity.noContent().build();
    }

    // Exception handler for ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Resource not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}