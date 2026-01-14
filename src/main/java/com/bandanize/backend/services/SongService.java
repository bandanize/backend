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
}
