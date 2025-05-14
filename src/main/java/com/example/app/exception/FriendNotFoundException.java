package com.example.app.exception;

public class FriendNotFoundException extends RuntimeException{
    public FriendNotFoundException(String message) {
        super(message);
    }
}
