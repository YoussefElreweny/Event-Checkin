package com.youssef.eventcheckin.event.dto;

import java.util.UUID;

public record EventStatsResponse(
        UUID eventId,
        String eventName,
        int capacity,
        long confirmedRegistrations,
        long checkedIn,
        long notYetArrived,
        double attendanceRate
) {}