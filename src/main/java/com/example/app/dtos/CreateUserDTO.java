package com.example.app.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDTO(
        @NotBlank String username,
        @Email @NotBlank String email,
        @NotBlank String password) {
}
