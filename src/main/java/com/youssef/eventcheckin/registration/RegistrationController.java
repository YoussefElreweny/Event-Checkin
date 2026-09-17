package com.youssef.eventcheckin.registration;


import com.youssef.eventcheckin.registration.dto.RegistrationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/events/")


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
