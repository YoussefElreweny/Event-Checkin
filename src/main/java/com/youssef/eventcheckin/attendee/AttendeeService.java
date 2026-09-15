package com.youssef.eventcheckin.attendee;

import com.youssef.eventcheckin.attendee.dto.AttendeeResponse;
import com.youssef.eventcheckin.attendee.dto.CreateAttendeeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendeeService {

    private final AttendeeRepository attendeeRepository;

    public AttendeeResponse create(CreateAttendeeRequest createAttendeeRequest) {

        if (attendeeRepository.existsByEmail(createAttendeeRequest.email())) {
            throw new RuntimeException("Email already exists");
        }

        Attendee attendee = new Attendee();

        attendee.setEmail(createAttendeeRequest.email());
        attendee.setFullName(createAttendeeRequest.fullName());

        Attendee savedAttendee = attendeeRepository.save(attendee);

        return toResponse(savedAttendee);
    }

    public AttendeeResponse getById(UUID id) {

        Attendee attendee = attendeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Attendee not found"));;

        return toResponse(attendee);
    }


    private AttendeeResponse toResponse(Attendee attendee) {

        return new AttendeeResponse(
                attendee.getId(),
                attendee.getFullName(),
                attendee.getEmail(),
                attendee.getPhone(),
                attendee.getCreatedAt()
        );
    }
}