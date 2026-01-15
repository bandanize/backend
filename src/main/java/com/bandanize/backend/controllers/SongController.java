package com.bandanize.backend.controllers;

import com.bandanize.backend.models.*;
import com.bandanize.backend.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SongController {

    @Autowired
    private SongService songService;

    // --- Song Lists ---
    // --- Song Lists ---
    @PostMapping("/bands/{bandId}/songlists")
    public ResponseEntity<SongListModel> createSongList(@PathVariable Long bandId,
            @RequestBody SongListModel songList,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        return ResponseEntity.ok(songService.createSongList(bandId, songList, userDetails.getUsername()));
    }

    @GetMapping("/bands/{bandId}/songlists")
    public ResponseEntity<List<SongListModel>> getSongLists(@PathVariable Long bandId) {
        return ResponseEntity.ok(songService.getSongListsByBand(bandId));
    }

    @PutMapping("/songlists/{listId}")
    public ResponseEntity<SongListModel> updateSongList(@PathVariable Long listId, @RequestBody SongListModel details,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        return ResponseEntity.ok(songService.updateSongList(listId, details, userDetails.getUsername()));
    }

    @DeleteMapping("/songlists/{listId}")
    public ResponseEntity<Void> deleteSongList(@PathVariable Long listId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        songService.deleteSongList(listId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // --- Songs ---
    @PostMapping("/songlists/{listId}/songs")
    public ResponseEntity<SongModel> addSong(@PathVariable Long listId, @RequestBody SongModel song,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        return ResponseEntity.ok(songService.addSong(listId, song, userDetails.getUsername()));
    }

    @PutMapping("/songs/{songId}")
    public ResponseEntity<SongModel> updateSong(@PathVariable Long songId, @RequestBody SongModel details,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        return ResponseEntity.ok(songService.updateSong(songId, details, userDetails.getUsername()));
    }

    @DeleteMapping("/songs/{songId}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long songId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        songService.deleteSong(songId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // --- Tablatures ---
    @PostMapping("/songs/{songId}/tabs")
    public ResponseEntity<TablatureModel> addTablature(@PathVariable Long songId, @RequestBody TablatureModel tab,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        return ResponseEntity.ok(songService.addTablature(songId, tab, userDetails.getUsername()));
    }

    @PutMapping("/tabs/{tabId}")
    public ResponseEntity<TablatureModel> updateTablature(@PathVariable Long tabId,
            @RequestBody TablatureModel details,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        return ResponseEntity.ok(songService.updateTablature(tabId, details, userDetails.getUsername()));
    }

    @DeleteMapping("/tabs/{tabId}")
    public ResponseEntity<Void> deleteTablature(@PathVariable Long tabId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        songService.deleteTablature(tabId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // --- Media Files ---
    @PostMapping("/songs/{songId}/files")
    public ResponseEntity<SongModel> addSongFile(@PathVariable Long songId, @RequestBody MediaFile file,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        return ResponseEntity.ok(songService.addFileToSong(songId, file, userDetails.getUsername()));
    }

    @PostMapping("/tabs/{tabId}/files")
    public ResponseEntity<TablatureModel> addTablatureFile(@PathVariable Long tabId, @RequestBody MediaFile file,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        return ResponseEntity.ok(songService.addFileToTablature(tabId, file, userDetails.getUsername()));
    }

    @DeleteMapping("/songs/{songId}/files")
    public ResponseEntity<SongModel> deleteSongFile(@PathVariable Long songId, @RequestParam String url,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        return ResponseEntity.ok(songService.removeFileFromSong(songId, url, userDetails.getUsername()));
    }

    @DeleteMapping("/tabs/{tabId}/files")
    public ResponseEntity<TablatureModel> deleteTablatureFile(@PathVariable Long tabId, @RequestParam String url,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        return ResponseEntity.ok(songService.removeFileFromTablature(tabId, url, userDetails.getUsername()));
    }
}
