package com.youssef.eventcheckin.event.dto;

import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.UUID;

public record CreateEventRequest(

        @NotNull(message = "Organizer ID is required")
        UUID organizerId,

        @NotBlank(message = "Event name is required")
        @Size(max = 200, message = "Event name must not exceed 200 characters")
        String name,

        @Size(max = 255, message = "Venue must not exceed 255 characters")
        String venue,

        String description,

        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        Instant startsAt,

        @NotNull(message = "End time is required")
        Instant endsAt,

        @NotNull(message = "Check-in opening time is required")
        Instant checkInOpensAt,

        @NotNull(message = "Check-in closing time is required")
        Instant checkInClosesAt,

        @NotNull(message = "Capacity is required")
        @Positive(message = "Capacity must be greater than 0")
        Integer capacity
) {}
