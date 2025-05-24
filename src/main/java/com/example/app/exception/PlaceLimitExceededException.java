package com.example.app.exception;

public class PlaceLimitExceededException extends RuntimeException {
    public PlaceLimitExceededException(String message) {
        super(message);
    }
}