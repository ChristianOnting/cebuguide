package com.cebuguide.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
        @NotBlank(message = "The reset token is missing.")
        String token,

        @NotBlank(message = "Enter a new password.")
        @Pattern(regexp = ValidationRules.PASSWORD_REGEX, message = ValidationRules.PASSWORD_MESSAGE)
        String newPassword,

        @NotBlank(message = "Confirm your new password.")
        String confirmPassword) {
}
