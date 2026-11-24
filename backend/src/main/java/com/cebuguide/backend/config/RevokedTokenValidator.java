package com.cebuguide.backend.config;

import com.cebuguide.backend.model.User;
import com.cebuguide.backend.repository.RevokedTokenRepository;
import com.cebuguide.backend.repository.UserRepository;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

/**
 * Rejects tokens that were logged out (revoked), tokens of accounts that no longer exist, and tokens that
 * were issued before the user's last password change.
 */
@Component
public class RevokedTokenValidator implements OAuth2TokenValidator<Jwt> {

    private final RevokedTokenRepository revokedTokens;
    private final UserRepository users;

    public RevokedTokenValidator(RevokedTokenRepository revokedTokens, UserRepository users) {
        this.revokedTokens = revokedTokens;
        this.users = users;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (jwt.getId() != null && revokedTokens.existsById(jwt.getId())) {
            return failure("The token has been revoked.");
        }
        Optional<User> user = parseId(jwt.getSubject()).flatMap(users::findById);
        if (user.isEmpty()) {
            return failure("The account no longer exists.");
        }
        Instant changedAt = user.get().getPasswordChangedAt();
        if (changedAt != null && jwt.getIssuedAt() != null && jwt.getIssuedAt().isBefore(changedAt)) {
            return failure("The password was changed. Please log in again.");
        }
        return OAuth2TokenValidatorResult.success();
    }

    private static Optional<Long> parseId(String subject) {
        try {
            return Optional.of(Long.valueOf(subject));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static OAuth2TokenValidatorResult failure(String description) {
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", description, null));
    }
}
