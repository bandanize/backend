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

    /**
     * Public iCal subscription endpoint.
     * Returns a live .ics feed that calendar apps can subscribe to.
     * No authentication required — calendar apps can't send JWT tokens.
     */
    @GetMapping(value = "/bands/{bandId}/calendar.ics", produces = "text/calendar;charset=UTF-8")
    public ResponseEntity<String> getCalendarFeed(@PathVariable Long bandId) {
        List<EventModel> events = eventService.getEventsByBand(bandId);
        StringBuilder ical = new StringBuilder();
        ical.append("BEGIN:VCALENDAR\r\n");
        ical.append("VERSION:2.0\r\n");
        ical.append("PRODID:-//Bandanize//Calendar//EN\r\n");
        ical.append("CALSCALE:GREGORIAN\r\n");
        ical.append("METHOD:PUBLISH\r\n");
        ical.append("X-WR-CALNAME:Bandanize\r\n");
        ical.append("REFRESH-INTERVAL;VALUE=DURATION:PT15M\r\n");
        ical.append("X-PUBLISHED-TTL:PT15M\r\n");

        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

        for (EventModel event : events) {
            ical.append("BEGIN:VEVENT\r\n");
            ical.append("UID:").append(event.getId()).append("@bandanize\r\n");
            ical.append("DTSTART:").append(event.getDate().format(dtf)).append("\r\n");
            ical.append("SUMMARY:").append(escapeIcal(event.getName())).append("\r\n");
            if (event.getDescription() != null && !event.getDescription().isEmpty()) {
                ical.append("DESCRIPTION:").append(escapeIcal(event.getDescription())).append("\r\n");
            }
            if (event.getLocation() != null && !event.getLocation().isEmpty()) {
                ical.append("LOCATION:").append(escapeIcal(event.getLocation())).append("\r\n");
            }
            ical.append("CATEGORIES:").append(event.getType()).append("\r\n");
            if (event.getCreatedAt() != null) {
                ical.append("CREATED:").append(event.getCreatedAt().format(dtf)).append("\r\n");
            }
            ical.append("END:VEVENT\r\n");
        }

        ical.append("END:VCALENDAR\r\n");

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"calendar.ics\"")
                .body(ical.toString());
    }

    private String escapeIcal(String text) {
        return text.replace("\\", "\\\\").replace(",", "\\,").replace(";", "\\;").replace("\n", "\\n");
    }
}
