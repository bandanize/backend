package com.bandanize.backend.services;

import com.bandanize.backend.exceptions.ResourceNotFoundException;
import com.bandanize.backend.models.*;
import com.bandanize.backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongService {
    @Autowired
    private SongListRepository songListRepository;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private TablatureRepository tablatureRepository;
    @Autowired
    private BandRepository bandRepository;

    // --- SongList ---
    public SongListModel createSongList(Long bandId, SongListModel songList) {
        BandModel band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found"));
        songList.setBand(band);
        return songListRepository.save(songList);
    }

    public List<SongListModel> getSongListsByBand(Long bandId) {
        return songListRepository.findByBandId(bandId);
    }

    public SongListModel updateSongList(Long listId, SongListModel details) {
        SongListModel list = songListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("SongList not found"));
        if (details.getName() != null) {
            list.setName(details.getName());
        }
        return songListRepository.save(list);
    }

    public void deleteSongList(Long listId) {
        songListRepository.deleteById(listId);
    }

    // --- Song ---
    public SongModel addSong(Long listId, SongModel song) {
        SongListModel list = songListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("SongList not found"));
        song.setSongList(list);
        return songRepository.save(song);
    }

    public SongModel updateSong(Long songId, SongModel details) {
        SongModel song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        if (details.getName() != null)
            song.setName(details.getName());
        if (details.getBpm() != 0)
            song.setBpm(details.getBpm());
        if (details.getSongKey() != null)
            song.setSongKey(details.getSongKey());
        if (details.getOriginalBand() != null)
            song.setOriginalBand(details.getOriginalBand());
        if (details.getFiles() != null) {
            song.setFiles(details.getFiles());
        }
        return songRepository.save(song);
    }

    public void deleteSong(Long songId) {
        songRepository.deleteById(songId);
    }

    // --- Tablature ---
    public TablatureModel addTablature(Long songId, TablatureModel tab) {
        SongModel song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        tab.setSong(song);
        return tablatureRepository.save(tab);
    }

    public TablatureModel updateTablature(Long tabId, TablatureModel details) {
        TablatureModel tab = tablatureRepository.findById(tabId)
                .orElseThrow(() -> new ResourceNotFoundException("Tablature not found"));
        if (details.getName() != null)
            tab.setName(details.getName());
        if (details.getInstrument() != null)
            tab.setInstrument(details.getInstrument());
        if (details.getInstrumentIcon() != null)
            tab.setInstrumentIcon(details.getInstrumentIcon());
        if (details.getTuning() != null)
            tab.setTuning(details.getTuning());
        if (details.getContent() != null)
            tab.setContent(details.getContent());
        if (details.getFiles() != null) {
            tab.setFiles(details.getFiles());
        }
        return tablatureRepository.save(tab);
    }

    public void deleteTablature(Long tabId) {
        tablatureRepository.deleteById(tabId);
    }

    public SongModel addFileToSong(Long songId, MediaFile file) {
        SongModel song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        song.getFiles().add(file);
        return songRepository.save(song);
    }

    public TablatureModel addFileToTablature(Long tabId, MediaFile file) {
        TablatureModel tab = tablatureRepository.findById(tabId)
                .orElseThrow(() -> new ResourceNotFoundException("Tablature not found"));
        tab.getFiles().add(file);
        return tablatureRepository.save(tab);
    }

    @Autowired
    private StorageService storageService;

    public SongModel removeFileFromSong(Long songId, String fileUrl) {
        SongModel song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        MediaFile fileToRemove = song.getFiles().stream()
                .filter(f -> f.getUrl().equals(fileUrl))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        // Delete from storage
        deleteFileFromStorage(fileToRemove.getUrl());

        // Remove from DB
        song.getFiles().remove(fileToRemove);
        return songRepository.save(song);
    }

    public TablatureModel removeFileFromTablature(Long tabId, String fileUrl) {
        TablatureModel tab = tablatureRepository.findById(tabId)
                .orElseThrow(() -> new ResourceNotFoundException("Tablature not found"));

        MediaFile fileToRemove = tab.getFiles().stream()
                .filter(f -> f.getUrl().equals(fileUrl))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        // Delete from storage
        deleteFileFromStorage(fileToRemove.getUrl());

        // Remove from DB
        tab.getFiles().remove(fileToRemove);
        return tablatureRepository.save(tab);
    }

    private void deleteFileFromStorage(String fileUrl) {
        // Expected URL format: /uploads/{folder}/{filename}
        try {
            String[] parts = fileUrl.split("/");
            if (parts.length >= 2) {
                String filename = parts[parts.length - 1];
                String folder = parts[parts.length - 2];
                storageService.deleteFile(filename, folder);
            }
        } catch (Exception e) {
            // Log warning but don't fail the operation? Or fail?
            // Failing is safer to keep consistency, but if file is already gone, maybe not.
            // Let's let it throw for now.
            throw new RuntimeException("Failed to delete file from storage: " + e.getMessage());
        }
    }
}
