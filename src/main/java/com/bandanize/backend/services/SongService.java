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

    private void verifyMember(BandModel band, String username) {
        boolean isMember = band.getUsers().stream()
                .anyMatch(u -> u.getUsername().equals(username));
        if (!isMember) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not a member of this band (" + band.getName() + ")");
        }
    }

    // --- SongList ---
    public SongListModel createSongList(Long bandId, SongListModel songList, String username) {
        BandModel band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found"));
        verifyMember(band, username);
        songList.setBand(band);
        return songListRepository.save(songList);
    }

    public List<SongListModel> getSongListsByBand(Long bandId) {
        return songListRepository.findByBandId(bandId);
    }

    public SongListModel updateSongList(Long listId, SongListModel details, String username) {
        SongListModel list = songListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("SongList not found"));
        verifyMember(list.getBand(), username);

        if (details.getName() != null) {
            list.setName(details.getName());
        }
        return songListRepository.save(list);
    }

    public void deleteSongList(Long listId, String username) {
        SongListModel list = songListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("SongList not found"));
        verifyMember(list.getBand(), username);
        songListRepository.deleteById(listId);
    }

    // --- Song ---
    public SongModel addSong(Long listId, SongModel song, String username) {
        SongListModel list = songListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("SongList not found"));
        verifyMember(list.getBand(), username);
        song.setSongList(list);
        return songRepository.save(song);
    }

    public SongModel updateSong(Long songId, SongModel details, String username) {
        SongModel song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        verifyMember(song.getSongList().getBand(), username);

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

    public void deleteSong(Long songId, String username) {
        SongModel song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        verifyMember(song.getSongList().getBand(), username);
        songRepository.deleteById(songId);
    }

    // --- Tablature ---
    public TablatureModel addTablature(Long songId, TablatureModel tab, String username) {
        SongModel song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        verifyMember(song.getSongList().getBand(), username);
        tab.setSong(song);
        return tablatureRepository.save(tab);
    }

    public TablatureModel updateTablature(Long tabId, TablatureModel details, String username) {
        TablatureModel tab = tablatureRepository.findById(tabId)
                .orElseThrow(() -> new ResourceNotFoundException("Tablature not found"));
        verifyMember(tab.getSong().getSongList().getBand(), username);

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

    public void deleteTablature(Long tabId, String username) {
        TablatureModel tab = tablatureRepository.findById(tabId)
                .orElseThrow(() -> new ResourceNotFoundException("Tablature not found"));
        verifyMember(tab.getSong().getSongList().getBand(), username);
        tablatureRepository.deleteById(tabId);
    }

    public SongModel addFileToSong(Long songId, MediaFile file, String username) {
        SongModel song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        verifyMember(song.getSongList().getBand(), username);
        song.getFiles().add(file);
        return songRepository.save(song);
    }

    public TablatureModel addFileToTablature(Long tabId, MediaFile file, String username) {
        TablatureModel tab = tablatureRepository.findById(tabId)
                .orElseThrow(() -> new ResourceNotFoundException("Tablature not found"));
        verifyMember(tab.getSong().getSongList().getBand(), username);
        tab.getFiles().add(file);
        return tablatureRepository.save(tab);
    }

    @Autowired
    private StorageService storageService;

    public SongModel removeFileFromSong(Long songId, String fileUrl, String username) {
        SongModel song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        verifyMember(song.getSongList().getBand(), username);

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

    public TablatureModel removeFileFromTablature(Long tabId, String fileUrl, String username) {
        TablatureModel tab = tablatureRepository.findById(tabId)
                .orElseThrow(() -> new ResourceNotFoundException("Tablature not found"));
        verifyMember(tab.getSong().getSongList().getBand(), username);

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
