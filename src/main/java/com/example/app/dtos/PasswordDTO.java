package com.example.app.dtos;

import jakarta.validation.constraints.NotBlank;

public record PasswordDTO(@NotBlank String password){
}
