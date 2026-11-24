package com.cebuguide.backend.controller;

import com.cebuguide.backend.dto.ChangePasswordRequest;
import com.cebuguide.backend.dto.MessageResponse;
import com.cebuguide.backend.service.PasswordService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Change password for the logged-in user (any role). */
@RestController
@RequestMapping("/api/profile")
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @PostMapping("/change-password")
    public MessageResponse changePassword(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ChangePasswordRequest request) {
        passwordService.changePassword(jwt, request);
        return new MessageResponse("Password changed. Please log in again with your new password.");
    }
}
