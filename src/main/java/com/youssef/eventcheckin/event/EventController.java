package com.youssef.eventcheckin.event;

import com.youssef.eventcheckin.event.dto.CreateEventRequest;
import com.youssef.eventcheckin.event.dto.EventResponse;
import com.youssef.eventcheckin.event.dto.EventStatsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse create(
            @Valid @RequestBody CreateEventRequest request) {

        return eventService.create(request);
    }

    @PostMapping("/{id}/publish")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse publish(@PathVariable UUID id) {
        return eventService.publish(id);
    }

    @GetMapping("/{id}")
    public EventResponse getById(@PathVariable UUID id) {

        return eventService.getById(id);
    }

    @DeleteMapping("/{eventId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable UUID id) {
        eventService.delete(id);
    }

    @GetMapping
    public Page<EventResponse> getAll(Pageable pageable) {

        return eventService.getAll(pageable);
    }

    @GetMapping("/{eventId}/stats")
    public EventStatsResponse getStats(@PathVariable UUID eventId) {

        return eventService.getStats(eventId);
    }
}