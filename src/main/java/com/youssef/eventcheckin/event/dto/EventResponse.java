package com.youssef.eventcheckin.event.dto;

import com.youssef.eventcheckin.event.EventStatus;

import java.time.Instant;
import java.util.UUID;

public record EventResponse(

        UUID id,
        UUID organizerId,
        String organizerName,
        String name,
        String venue,
        String description,
        Instant startsAt,
        Instant endsAt,
        Instant checkInOpensAt,
        Instant checkInClosesAt,
        Integer capacity,
        EventStatus status,
        Instant createdAt,
        Instant updatedAt

) {}
