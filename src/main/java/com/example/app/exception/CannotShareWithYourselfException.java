package com.example.app.exception;

public class CannotShareWithYourselfException extends RuntimeException {
    public CannotShareWithYourselfException(String message) {
        super(message);
    }
}
