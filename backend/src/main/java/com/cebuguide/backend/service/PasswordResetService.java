package com.cebuguide.backend.service;

import com.cebuguide.backend.dto.ResetPasswordRequest;
import com.cebuguide.backend.exception.InvalidResetTokenException;
import com.cebuguide.backend.model.PasswordResetToken;
import com.cebuguide.backend.model.User;
import com.cebuguide.backend.repository.PasswordResetTokenRepository;
import com.cebuguide.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class PasswordResetService {

    /** The same answer is returned whether or not the email belongs to an account. */
    public static final String GENERIC_MESSAGE =
            "If an account exists for that email address, a password reset link has been sent. Check your inbox.";

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final PasswordService passwordService;
    private final EmailService emailService;
    private final long tokenMinutes;

    public PasswordResetService(UserRepository users, PasswordResetTokenRepository tokens,
                                PasswordService passwordService, EmailService emailService,
                                @Value("${app.reset-token-minutes}") long tokenMinutes) {
        this.users = users;
        this.tokens = tokens;
        this.passwordService = passwordService;
        this.emailService = emailService;
        this.tokenMinutes = tokenMinutes;
    }

    /** Creates a reset token for the account and emails the link. Older tokens of the account stop working. */
    @Transactional
    public void requestReset(String rawEmail) {
        tokens.deleteByExpiresAtBefore(Instant.now());
        users.findByEmailIgnoreCase(rawEmail.trim()).ifPresent(user -> {
            tokens.deleteByUserId(user.getId());
            String rawToken = newToken();
            tokens.save(new PasswordResetToken(hash(rawToken), user, Instant.now().plus(tokenMinutes, ChronoUnit.MINUTES)));
            emailService.sendPasswordReset(user, rawToken);
        });
    }

    /** Lets the reset page tell the visitor early whether the link can still be used. */
    @Transactional(readOnly = true)
    public void validate(String rawToken) {
        findUsable(rawToken);
    }

    /** Sets the new password and deletes the token, so the link works only once. */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordService.requireMatch(request.newPassword(), request.confirmPassword());
        PasswordResetToken token = findUsable(request.token());
        User user = token.getUser();
        passwordService.updatePassword(user, request.newPassword());
        tokens.deleteByUserId(user.getId());
    }

    private PasswordResetToken findUsable(String rawToken) {
        return tokens.findByTokenHash(hash(rawToken))
                .filter(t -> t.isUsable(Instant.now()))
                .orElseThrow(InvalidResetTokenException::new);
    }

    private static String newToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(rawToken.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
