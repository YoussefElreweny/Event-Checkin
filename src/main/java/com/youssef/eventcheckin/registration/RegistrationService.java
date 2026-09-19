package com.youssef.eventcheckin.registration;


import com.youssef.eventcheckin.attendee.Attendee;
import com.youssef.eventcheckin.attendee.AttendeeRepository;
import com.youssef.eventcheckin.checkin.CheckInRepository;
import com.youssef.eventcheckin.common.exception.ConflictException;
import com.youssef.eventcheckin.common.exception.NotFoundException;
import com.youssef.eventcheckin.event.Event;
import com.youssef.eventcheckin.event.EventRepository;
import com.youssef.eventcheckin.event.EventStatus;
import com.youssef.eventcheckin.registration.dto.CreateRegistrationRequest;
import com.youssef.eventcheckin.registration.dto.RegistrationResponse;
import com.youssef.eventcheckin.ticket.Ticket;
import com.youssef.eventcheckin.ticket.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final AttendeeRepository attendeeRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final TicketRepository ticketRepository;
    private final CheckInRepository checkInRepository;



    @Transactional
    public RegistrationResponse register(UUID eventId , CreateRegistrationRequest request){

        // 1. Load Event
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));

        // 2. Load attendee
        Attendee attendee = attendeeRepository.findById(request.attendeeId()).orElseThrow(() -> new NotFoundException("Attendee not found"));

        //3. Check event status
        if (event.getStatus() !=  EventStatus.PUBLISHED) {
            throw new IllegalStateException("Event is not open for registration");
        }

        //4. check capacity
        long confirmedRegistration = registrationRepository.countByEventIdAndStatus(event.getId(),RegistrationStatus.CONFIRMED);

        if (confirmedRegistration >= event.getCapacity()){
            throw new ConflictException("Event is at capacity");
        }

        // 5. Create & save registration
        Registration registration = new Registration();
        registration.setEvent(event);
        registration.setAttendee(attendee);
        registration.setStatus(RegistrationStatus.CONFIRMED);
        registrationRepository.save(registration);

        // 7. Create & save ticket
        Ticket ticket = new Ticket();
        ticket.setRegistration(registration);
        ticket.setTicketCode(UUID.randomUUID().toString());
        ticketRepository.save(ticket);

        return RegistrationResponse.from(registration, ticket);
    }


    @Transactional(readOnly = true)
    public RegistrationResponse getRegistration(UUID id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Registration not found"));

        Ticket ticket = ticketRepository.findByRegistrationId(registration.getId())
                .orElseThrow(() -> new NotFoundException("Ticket not found"));

        return RegistrationResponse.from(registration, ticket);
    }


    @Transactional
    public void cancel(UUID registrationId) {

        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new NotFoundException("Registration not found"));

        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new ConflictException("Registration is already cancelled");
        }

        Ticket ticket = ticketRepository.findByRegistrationId(registrationId)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));

        if (checkInRepository.existsByTicketId(ticket.getId())) {
            throw new ConflictException("Cannot cancel a registration that has already been checked in");
        }

        registration.setStatus(RegistrationStatus.CANCELLED);
        registration.setCancelledAt(Instant.now());
        ticket.setRevokedAt(Instant.now());
    }


    @Transactional(readOnly = true)
    public Page<RegistrationResponse> getEventRegistrations(UUID eventId, Pageable pageable) {

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));

        Page<Registration> registrations = registrationRepository.findByEventId(eventId, pageable);


        return registrations.map(registration -> {
            Ticket ticket = ticketRepository.findByRegistrationId(registration.getId())
                    .orElseThrow(() -> new NotFoundException("Ticket not found"));

            return RegistrationResponse.from(registration, ticket);
        });
    }
}
