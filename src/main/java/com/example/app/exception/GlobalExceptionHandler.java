package com.example.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUsernameNotFound(UsernameNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }
    @ExceptionHandler(FriendshipNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleFriendshipNotFound(FriendshipNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(InvitationAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleInvitationAlreadyExists(InvitationAlreadyExistsException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(CannotInviteYourselfException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCannotInviteYourself(CannotInviteYourselfException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(InvitationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleInvitationNotFound(InvitationNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(UserNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleRoleNotFound(RoleNotFoundException ex) {
        return Map.of("error", "Konfiguracja ról: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneric(Exception ex) {
        return Map.of("error", "Wystąpił błąd: " + ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailAlreadyUsed(EmailAlreadyUsedException ex) {
        return Map.of("error", ex.getMessage());
    }
}
