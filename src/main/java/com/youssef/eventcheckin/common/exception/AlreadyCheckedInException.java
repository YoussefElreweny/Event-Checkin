package com.youssef.eventcheckin.common.exception;

public class AlreadyCheckedInException extends ConflictException {

    public AlreadyCheckedInException(String message) {
        super(message);
    }
}
