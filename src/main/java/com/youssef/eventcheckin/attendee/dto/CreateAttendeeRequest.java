package com.youssef.eventcheckin.attendee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAttendeeRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email address")
        String email,

        @NotBlank(message = "Full name is required")
        @Size(max = 150)
        String fullName,

        String phone
) {}
