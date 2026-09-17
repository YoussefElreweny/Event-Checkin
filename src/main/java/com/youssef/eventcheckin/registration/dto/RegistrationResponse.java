package com.youssef.eventcheckin.registration.dto;

import com.youssef.eventcheckin.registration.Registration;
import com.youssef.eventcheckin.registration.RegistrationStatus;
import com.youssef.eventcheckin.ticket.Ticket;

import java.time.Instant;
import java.util.UUID;

public record RegistrationResponse(
        UUID id,
        UUID event,
        UUID attendee,
        RegistrationStatus status,
        Instant registeredAt,
        Instant cancelledAt,
        String ticketcode
) {
    public static RegistrationResponse from(Registration registration, Ticket ticket) {
        return new RegistrationResponse(
                registration.getId(),
                registration.getEvent().getId(),
                registration.getAttendee().getId(),
                registration.getStatus(),
                registration.getRegisteredAt(),
                registration.getCancelledAt(),
                ticket.getTicketCode()
        );
    }
}
