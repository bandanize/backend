package com.bandanize.backend.controllers;

import com.bandanize.backend.exceptions.ErrorResponse;
import com.bandanize.backend.exceptions.ResourceNotFoundException;
import com.bandanize.backend.models.TabModel;
import com.bandanize.backend.repositories.TabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tabs")
public class TabController {

    @Autowired
    private TabRepository tabRepository;

    // Get all tabs
    @GetMapping
    public List<TabModel> getAllTabs() {
        return tabRepository.findAll();
    }

    // Get a tab by ID
    @GetMapping("/{id}")
    public TabModel getTabById(@PathVariable Long id) {
        return tabRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tab not found with id: " + id));
    }

    // Create a new tab
    @PostMapping
    public TabModel createTab(@RequestBody TabModel tab) {
        return tabRepository.save(tab);
    }

    // Update an existing tab
    @PutMapping("/{id}")
    public TabModel updateTab(@PathVariable Long id, @RequestBody TabModel tabDetails) {
        TabModel tab = tabRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tab not found with id: " + id));

        tab.setTitle(tabDetails.getTitle());
        tab.setInstrument(tabDetails.getInstrument());
        tab.setTuning(tabDetails.getTuning());
        tab.setData(tabDetails.getData());
        tab.setSongId(tabDetails.getSongId());

        return tabRepository.save(tab);
    }

    // Delete a tab
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTab(@PathVariable Long id) {
        TabModel tab = tabRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tab not found with id: " + id));
        tabRepository.delete(tab);
        return ResponseEntity.noContent().build();
    }

    // Exception handler for ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Resource not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}