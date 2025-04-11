package com.example.app.exception;

public class CannotInviteYourselfException extends RuntimeException {
    public CannotInviteYourselfException(String message) {
        super(message);
    }
}