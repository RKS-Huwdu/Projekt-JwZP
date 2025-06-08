package com.example.app.exception;

public class LogFileNotFoundException extends RuntimeException {
    public LogFileNotFoundException(String message) {
        super(message);
    }
}
