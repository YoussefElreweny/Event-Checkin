package com.youssef.eventcheckin.event;

import com.youssef.eventcheckin.event.dto.CreateEventRequest;
import com.youssef.eventcheckin.event.dto.EventResponse;
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

    @GetMapping("/{id}")
    public EventResponse getById(@PathVariable UUID id) {

        return eventService.getById(id);
    }

    @GetMapping
    public Page<EventResponse> getAll(Pageable pageable) {

        return eventService.getAll(pageable);
    }
}