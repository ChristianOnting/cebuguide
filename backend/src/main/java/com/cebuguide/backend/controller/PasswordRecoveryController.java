package com.cebuguide.backend.controller;

import com.cebuguide.backend.dto.ForgotPasswordRequest;
import com.cebuguide.backend.dto.MessageResponse;
import com.cebuguide.backend.dto.ResetPasswordRequest;
import com.cebuguide.backend.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Forgot password and reset password. These endpoints are public because the user cannot log in. */
@RestController
@RequestMapping("/api/auth")
public class PasswordRecoveryController {

    private final PasswordResetService resetService;

    public PasswordRecoveryController(PasswordResetService resetService) {
        this.resetService = resetService;
    }

    @PostMapping("/forgot-password")
    public MessageResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        resetService.requestReset(request.email());
        return new MessageResponse(PasswordResetService.GENERIC_MESSAGE);
    }

    @GetMapping("/reset-password/validate")
    public MessageResponse validate(@RequestParam("token") String token) {
        resetService.validate(token);
        return new MessageResponse("The reset link is valid.");
    }

    @PostMapping("/reset-password")
    public MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetService.resetPassword(request);
        return new MessageResponse("Your password has been reset. You can now log in.");
    }
}
