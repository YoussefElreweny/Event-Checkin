package com.youssef.eventcheckin.common.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Builder
@Getter
public class ErrorResponse {

    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final Map<String, String> fieldErrors;
}
