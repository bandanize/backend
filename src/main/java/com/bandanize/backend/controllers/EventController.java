package com.bandanize.backend.controllers;

import com.bandanize.backend.exceptions.ResourceNotFoundException;
import com.bandanize.backend.models.EventModel;
import com.bandanize.backend.repositories.UserRepository;
import com.bandanize.backend.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    private Long getCurrentUserId(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found")).getId();
    }

    @GetMapping("/bands/{bandId}/events")
    public ResponseEntity<List<EventModel>> getEvents(@PathVariable Long bandId) {
        return ResponseEntity.ok(eventService.getEventsByBand(bandId));
    }

    @PostMapping("/bands/{bandId}/events")
    public ResponseEntity<EventModel> createEvent(@PathVariable Long bandId, @RequestBody EventModel event,
            Principal principal) {
        return ResponseEntity.ok(eventService.createEvent(bandId, getCurrentUserId(principal), event));
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<EventModel> updateEvent(@PathVariable Long eventId, @RequestBody EventModel event,
            Principal principal) {
        return ResponseEntity.ok(eventService.updateEvent(eventId, getCurrentUserId(principal), event));
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok().build();
    }
}
