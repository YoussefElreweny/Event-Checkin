package com.youssef.eventcheckin.registration.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateRegistrationRequest(

        @NotNull(message = "Must include the Event")
        UUID eventId,

        @NotNull(message = "Must include the attendee")
        UUID attendeeId

) {}
