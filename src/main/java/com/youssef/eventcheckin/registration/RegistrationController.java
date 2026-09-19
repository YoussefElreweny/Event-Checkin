package com.youssef.eventcheckin.registration;


import com.youssef.eventcheckin.registration.dto.CreateRegistrationRequest;
import com.youssef.eventcheckin.registration.dto.RegistrationResponse;
import com.youssef.eventcheckin.ticket.Ticket;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/events/{eventId}/registrations")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse register(
            @PathVariable UUID eventId,
            @Valid @RequestBody CreateRegistrationRequest request){

        return registrationService.register(eventId, request);
    }

    @DeleteMapping("/registrations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable UUID id) {
        registrationService.cancel(id);
    }


    @GetMapping("/events/{eventId}/registrations")
    public Page<RegistrationResponse> getEventRegistrations(
            @PathVariable UUID eventId,
            Pageable pageable) {

        return registrationService.getEventRegistrations(eventId, pageable);
    }

    @GetMapping("/registrations/{id}")
    public RegistrationResponse getRegistration(@PathVariable UUID id) {
        return registrationService.getRegistration(id);
    }

}
