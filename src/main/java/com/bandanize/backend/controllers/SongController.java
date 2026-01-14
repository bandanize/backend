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
    @PostMapping("/bands/{bandId}/songlists")
    public ResponseEntity<SongListModel> createSongList(@PathVariable Long bandId,
            @RequestBody SongListModel songList) {
        return ResponseEntity.ok(songService.createSongList(bandId, songList));
    }

    @GetMapping("/bands/{bandId}/songlists")
    public ResponseEntity<List<SongListModel>> getSongLists(@PathVariable Long bandId) {
        return ResponseEntity.ok(songService.getSongListsByBand(bandId));
    }

    @PutMapping("/songlists/{listId}")
    public ResponseEntity<SongListModel> updateSongList(@PathVariable Long listId, @RequestBody SongListModel details) {
        return ResponseEntity.ok(songService.updateSongList(listId, details));
    }

    @DeleteMapping("/songlists/{listId}")
    public ResponseEntity<Void> deleteSongList(@PathVariable Long listId) {
        songService.deleteSongList(listId);
        return ResponseEntity.ok().build();
    }

    // --- Songs ---
    @PostMapping("/songlists/{listId}/songs")
    public ResponseEntity<SongModel> addSong(@PathVariable Long listId, @RequestBody SongModel song) {
        return ResponseEntity.ok(songService.addSong(listId, song));
    }

    @PutMapping("/songs/{songId}")
    public ResponseEntity<SongModel> updateSong(@PathVariable Long songId, @RequestBody SongModel details) {
        return ResponseEntity.ok(songService.updateSong(songId, details));
    }

    @DeleteMapping("/songs/{songId}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long songId) {
        songService.deleteSong(songId);
        return ResponseEntity.ok().build();
    }

    // --- Tablatures ---
    @PostMapping("/songs/{songId}/tabs")
    public ResponseEntity<TablatureModel> addTablature(@PathVariable Long songId, @RequestBody TablatureModel tab) {
        return ResponseEntity.ok(songService.addTablature(songId, tab));
    }

    @PutMapping("/tabs/{tabId}")
    public ResponseEntity<TablatureModel> updateTablature(@PathVariable Long tabId,
            @RequestBody TablatureModel details) {
        return ResponseEntity.ok(songService.updateTablature(tabId, details));
    }

    @DeleteMapping("/tabs/{tabId}")
    public ResponseEntity<Void> deleteTablature(@PathVariable Long tabId) {
        songService.deleteTablature(tabId);
        return ResponseEntity.ok().build();
    }
}
