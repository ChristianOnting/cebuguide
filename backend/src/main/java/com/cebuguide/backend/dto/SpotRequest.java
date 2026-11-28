package com.cebuguide.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record SpotRequest(
        @NotBlank(message = "Name is required.")
        @Size(max = 150, message = "Name must be at most 150 characters.")
        String name,

        @NotBlank(message = "Description is required.")
        @Size(max = 2000, message = "Description must be at most 2000 characters.")
        String description,

        @NotNull(message = "Choose a category.")
        Long categoryId,

        @NotBlank(message = "Address is required.")
        @Size(max = 255, message = "Address must be at most 255 characters.")
        String address,

        @NotNull(message = "Latitude is required.")
        BigDecimal latitude,

        @NotNull(message = "Longitude is required.")
        BigDecimal longitude,

        Boolean indoor) {
}
