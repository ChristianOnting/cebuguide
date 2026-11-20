package com.cebuguide.backend.dto;

import com.cebuguide.backend.model.Role;
import com.cebuguide.backend.model.User;

import java.time.Instant;

public record UserResponse(Long id, String name, String email, Role role, Instant createdAt) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getCreatedAt());
    }
}
