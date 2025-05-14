package com.example.app.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record CreatePlaceDTO(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Category is required")
        String category,
        @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
        @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
        double latitude,
        @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
        @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
        double longitude,
        String address,
        String note,
        Boolean isPublic
) {}
