package com.youssef.eventcheckin.checkin;

import com.youssef.eventcheckin.checkin.dto.CreateCheckInRequest;
import com.youssef.eventcheckin.checkin.dto.CheckInResponse;
import com.youssef.eventcheckin.event.Event;
import com.youssef.eventcheckin.registration.Registration;
import com.youssef.eventcheckin.registration.RegistrationStatus;
import com.youssef.eventcheckin.ticket.Ticket;
import com.youssef.eventcheckin.ticket.TicketRepository;
import com.youssef.eventcheckin.user.User;
import com.youssef.eventcheckin.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final TicketRepository ticketRepository;
    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;


    @Transactional
    public CheckInResponse checkIn(UUID eventId, CreateCheckInRequest request) {

        // 1. Find ticket by code
        Ticket ticket = ticketRepository.findByTicketCode(request.ticketCode())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // 2. Check if ticket is revoked
        if (ticket.getRevokedAt() != null) {
            throw new RuntimeException("Ticket has been revoked");
        }

        Registration registration = ticket.getRegistration();

        // 3. Check ticket belongs to this event
        Event event = registration.getEvent();

        if (!event.getId().equals(eventId)) {
            throw new RuntimeException("Ticket does not belong to this event");
        }

        // 4. Check registration status
        if (registration.getStatus() != RegistrationStatus.CONFIRMED) {
            throw new RuntimeException("Registration is not confirmed");
        }

        // 5. Check check-in window
        Instant now = Instant.now();

        if (now.isBefore(event.getCheckInOpensAt())
                || now.isAfter(event.getCheckInClosesAt())) {
            throw new RuntimeException("Check-in is not currently open");
        }

        // 6. Check if already checked in
        if (checkInRepository.existsByTicketId(ticket.getId())) {
            throw new RuntimeException("Ticket has already been checked in");
        }

        // Find staff user
        User staffUser = userRepository.findById(request.staffUserId())
                .orElseThrow(() -> new RuntimeException("Staff user not found"));

        // 7. Save check-in
        CheckIn checkIn = new CheckIn();
        checkIn.setTicket(ticket);
        checkIn.setUser(staffUser);
        checkIn.setCheckedInAt(now);
        checkIn.setGate(request.gate());

        CheckIn savedCheckIn = checkInRepository.save(checkIn);

        return CheckInResponse.from(savedCheckIn);
    }


    @Transactional(readOnly = true)
    public Page<CheckInResponse> getAllCheckIns(Pageable pageable) {

        return checkInRepository.findAll(pageable).map(CheckInResponse::from);
    }

}