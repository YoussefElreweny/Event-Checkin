package com.youssef.eventcheckin.common.exception;

public class AlreadyCheckedInException extends RuntimeException {

    public AlreadyCheckedInException(String message) {
        super(message);
    }
}
