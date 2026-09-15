package com.youssef.eventcheckin.attendee.dto;

import java.time.Instant;
import java.util.UUID;

public record AttendeeResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        Instant createdAt
) {
}