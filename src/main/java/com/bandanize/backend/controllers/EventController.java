package com.bandanize.backend.controllers;

import com.bandanize.backend.dtos.EventDTO;
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
    public ResponseEntity<List<EventDTO>> getEvents(@PathVariable Long bandId) {
        List<EventDTO> dtos = eventService.getEventsByBand(bandId)
                .stream()
                .map(EventDTO::fromModel)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/bands/{bandId}/events")
    public ResponseEntity<EventDTO> createEvent(@PathVariable Long bandId, @RequestBody EventModel event,
            Principal principal) {
        EventModel created = eventService.createEvent(bandId, getCurrentUserId(principal), event);
        return ResponseEntity.ok(EventDTO.fromModel(created));
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long eventId, @RequestBody EventModel event,
            Principal principal) {
        EventModel updated = eventService.updateEvent(eventId, getCurrentUserId(principal), event);
        return ResponseEntity.ok(EventDTO.fromModel(updated));
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok().build();
    }
}
