package com.cebuguide.backend.dto;

import com.cebuguide.backend.model.Spot;

import java.math.BigDecimal;
import java.time.Instant;

public record SpotResponse(Long id, String name, String description, CategoryRef category, String address,
                           BigDecimal latitude, BigDecimal longitude, boolean indoor, boolean active,
                           Instant createdAt, Instant updatedAt) {
    public static SpotResponse from(Spot s) {
        return new SpotResponse(s.getId(), s.getName(), s.getDescription(), CategoryRef.from(s.getCategory()),
                s.getAddress(), s.getLatitude(), s.getLongitude(), s.isIndoor(), s.isActive(),
                s.getCreatedAt(), s.getUpdatedAt());
    }
}
