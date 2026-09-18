package com.youssef.eventcheckin.checkin.dto;


import com.youssef.eventcheckin.checkin.CheckIn;

import java.time.Instant;
import java.util.UUID;


public record CheckInResponse(
        UUID id,
        String ticketCode,
        UUID checkedInBy,
        Instant checkedInAt,
        String gate
) {

    public static CheckInResponse from(CheckIn checkIn) {

        return new CheckInResponse(
                checkIn.getId(),
                checkIn.getTicket().getTicketCode(),
                checkIn.getUser().getId(),
                checkIn.getCheckedInAt(),
                checkIn.getGate()
        );
    }
}