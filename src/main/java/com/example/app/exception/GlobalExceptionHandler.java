package com.example.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
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

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleCategoryNotFound(CategoryNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(PlaceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleCategoryNotFound(PlaceNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(FriendNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> friendNotFound(FriendNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleRoleNotFound(RoleNotFoundException ex) {
        return Map.of("error", "Konfiguracja ról: " + ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailAlreadyUsed(EmailAlreadyUsedException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleUsernameAlreadyUsed(UsernameAlreadyUsedException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleUserAlreadyExist(UserAlreadyExistsException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return errors;
    }

    @ExceptionHandler(ResourceOwnershipException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleOwnershipException(ResourceOwnershipException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(PlaceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handlePlaceAlreadyExists(PlaceAlreadyExistsException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(PlaceLimitExceededException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handlePlaceLimitExceeded(PlaceLimitExceededException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleCategoryAlreadyExists(CategoryAlreadyExistsException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(CannotShareWithYourselfException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCannotShareWithYourself(CannotShareWithYourselfException ex) {
        return Map.of("error", ex.getMessage());
    }
}
