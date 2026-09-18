package com.youssef.eventcheckin.checkin;


import com.youssef.eventcheckin.checkin.dto.CheckInResponse;
import com.youssef.eventcheckin.checkin.dto.CreateCheckInRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events/{eventId}/check-ins")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CheckInResponse checkIn(
            @PathVariable UUID checkInId,
            @Valid @RequestBody CreateCheckInRequest request) {

        return checkInService.checkIn(checkInId,request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<CheckInResponse> getAllCheckIns(Pageable pageable){

        return checkInService.getAllCheckIns(pageable);
    }
}
