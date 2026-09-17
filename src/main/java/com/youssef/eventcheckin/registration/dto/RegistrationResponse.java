package com.youssef.eventcheckin.registration.dto;

import com.youssef.eventcheckin.registration.Registration;
import com.youssef.eventcheckin.registration.RegistrationStatus;
import com.youssef.eventcheckin.ticket.Ticket;

import java.time.Instant;
import java.util.UUID;

public record RegistrationResponse(
        UUID id,
        UUID eventId,
        UUID attendeeId,
        String eventName,
        String attendeeName,
        RegistrationStatus status,
        Instant registeredAt,
        Instant cancelledAt,
        String ticketCode
) {
    public static RegistrationResponse from(Registration registration, Ticket ticket) {
        return new RegistrationResponse(
                registration.getId(),
                registration.getEvent().getId(),
                registration.getAttendee().getId(),
                registration.getEvent().getName(),
                registration.getEvent().getName(),
                registration.getStatus(),
                registration.getRegisteredAt(),
                registration.getCancelledAt(),
                ticket.getTicketCode()
        );
    }
}
