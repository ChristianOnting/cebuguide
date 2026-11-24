package com.cebuguide.backend.service;

import com.cebuguide.backend.dto.ChangePasswordRequest;
import com.cebuguide.backend.exception.FieldValidationException;
import com.cebuguide.backend.exception.InvalidCredentialsException;
import com.cebuguide.backend.model.RevokedToken;
import com.cebuguide.backend.model.User;
import com.cebuguide.backend.repository.RevokedTokenRepository;
import com.cebuguide.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class PasswordService {

    private final UserRepository users;
    private final RevokedTokenRepository revokedTokens;
    private final PasswordEncoder passwordEncoder;

    public PasswordService(UserRepository users, RevokedTokenRepository revokedTokens, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.revokedTokens = revokedTokens;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Changes the password of the logged-in user. The current password must be correct and the new
     * password must be valid, confirmed, and different. The current session is ended, so the user
     * logs in again with the new password.
     */
    @Transactional
    public void changePassword(Jwt jwt, ChangePasswordRequest request) {
        User user = users.findById(Long.valueOf(jwt.getSubject())).orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new FieldValidationException("currentPassword", "Current password is incorrect.");
        }
        requireMatch(request.newPassword(), request.confirmPassword());
        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new FieldValidationException("newPassword", "New password must be different from the current password.");
        }

        updatePassword(user, request.newPassword());

        if (!revokedTokens.existsById(jwt.getId())) {
            revokedTokens.save(new RevokedToken(jwt.getId(), jwt.getExpiresAt()));
        }
    }

    /** Stores the BCrypt hash of the new password and invalidates every token issued before now. */
    public void updatePassword(User user, String newPassword) {
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        users.save(user);
    }

    public static void requireMatch(String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new FieldValidationException("confirmPassword", "Passwords do not match.");
        }
    }
}
