package com.example.app.dtos;

import jakarta.validation.constraints.Email;

public record UpdateUserDTO(
        String username,
        @Email String email) {
}
