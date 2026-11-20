package com.cebuguide.backend.config;

import com.cebuguide.backend.repository.RevokedTokenRepository;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/** Rejects tokens that were logged out (revoked) before their expiry time. */
@Component
public class RevokedTokenValidator implements OAuth2TokenValidator<Jwt> {

    private final RevokedTokenRepository revokedTokens;

    public RevokedTokenValidator(RevokedTokenRepository revokedTokens) {
        this.revokedTokens = revokedTokens;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (jwt.getId() != null && revokedTokens.existsById(jwt.getId())) {
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "The token has been revoked.", null));
        }
        return OAuth2TokenValidatorResult.success();
    }
}
