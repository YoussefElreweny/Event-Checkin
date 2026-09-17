package com.youssef.eventcheckin.event;

import com.youssef.eventcheckin.event.dto.CreateEventRequest;
import com.youssef.eventcheckin.event.dto.EventResponse;
import com.youssef.eventcheckin.user.User;
import com.youssef.eventcheckin.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public EventResponse create(CreateEventRequest request) {

        if (!request.endsAt().isAfter(request.startsAt())) {
            throw new RuntimeException("End time must be after start time");
        }

        if (!request.checkInClosesAt().isAfter(request.checkInOpensAt())) {
            throw new RuntimeException("Check-in closing time must be after opening time");
        }

        if (request.checkInOpensAt().isBefore(request.startsAt())
                || request.checkInClosesAt().isAfter(request.endsAt())) {
            throw new RuntimeException("Check-in window must be within the event time");
        }

        User organizer = userRepository.findById(request.organizerId())
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        Event event = new Event();

        event.setOrganizer(organizer);
        event.setName(request.name());
        event.setVenue(request.venue());
        event.setDescription(request.description());
        event.setStartsAt(request.startsAt());
        event.setEndsAt(request.endsAt());
        event.setCheckInOpensAt(request.checkInOpensAt());
        event.setCheckInClosesAt(request.checkInClosesAt());
        event.setCapacity(request.capacity());

        Event savedEvent = eventRepository.save(event);

        return toResponse(savedEvent);
    }

    @Transactional(readOnly = true)
    public EventResponse getById(UUID id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return toResponse(event);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> getAll(Pageable pageable) {

        return eventRepository.findAll(pageable)
                .map(this::toResponse);
    }

    private EventResponse toResponse(Event event) {

        return new EventResponse(
                event.getId(),
                event.getOrganizer().getId(),
                event.getOrganizer().getFullName(),
                event.getName(),
                event.getVenue(),
                event.getDescription(),
                event.getStartsAt(),
                event.getEndsAt(),
                event.getCheckInOpensAt(),
                event.getCheckInClosesAt(),
                event.getCapacity(),
                event.getStatus(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}