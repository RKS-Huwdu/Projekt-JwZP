package com.example.app.exception;

public class PlaceNotFoundException extends RuntimeException {
    public PlaceNotFoundException(String message) {
        super(message);
    }
}
