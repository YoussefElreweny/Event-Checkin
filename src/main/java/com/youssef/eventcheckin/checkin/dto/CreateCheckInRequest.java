package com.youssef.eventcheckin.checkin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

public record CreateCheckInRequest(

        @NotNull
        String ticketCode,

        String gate,

        @NotNull
        UUID staffUserId

) {}
