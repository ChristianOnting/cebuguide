package com.cebuguide.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordRequest(
        @NotBlank(message = "Enter your current password.")
        String currentPassword,

        @NotBlank(message = "Enter a new password.")
        @Pattern(regexp = ValidationRules.PASSWORD_REGEX, message = ValidationRules.PASSWORD_MESSAGE)
        String newPassword,

        @NotBlank(message = "Confirm your new password.")
        String confirmPassword) {
}
