package com.youssef.eventcheckin.attendee;

import com.youssef.eventcheckin.attendee.dto.AttendeeResponse;
import com.youssef.eventcheckin.attendee.dto.CreateAttendeeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendeeService {

    private final AttendeeRepository attendeeRepository;

    @Transactional
    public AttendeeResponse create(CreateAttendeeRequest createAttendeeRequest) {

        if (attendeeRepository.existsByEmail(createAttendeeRequest.email())) {
            throw new RuntimeException("Email already exists");
        }

        Attendee attendee = new Attendee();

        attendee.setEmail(createAttendeeRequest.email());
        attendee.setFullName(createAttendeeRequest.fullName());
        attendee.setPhone(createAttendeeRequest.phone());


        Attendee savedAttendee = attendeeRepository.save(attendee);

        return toResponse(savedAttendee);
    }

    @Transactional
    public AttendeeResponse getById(UUID id) {

        Attendee attendee = attendeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Attendee not found"));

        return toResponse(attendee);
    }


    @Transactional(readOnly = true)
    public Page<AttendeeResponse> getAll(Pageable pageable) {
        return attendeeRepository.findAll(pageable)
                .map(this::toResponse);
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