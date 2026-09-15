package com.youssef.eventcheckin.attendee;


import com.youssef.eventcheckin.attendee.dto.AttendeeResponse;
import com.youssef.eventcheckin.attendee.dto.CreateAttendeeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendees")
@RequiredArgsConstructor
@Slf4j
public class AttendeeController {

    private final AttendeeService attendeeService;


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public AttendeeResponse create(@Valid @RequestBody CreateAttendeeRequest createAttendeeRequest) {
        return attendeeService.create(createAttendeeRequest);
    }

    @GetMapping("/{id}")
    public AttendeeResponse getById(@PathVariable UUID id) {
        return attendeeService.getById(id);
    }

}
