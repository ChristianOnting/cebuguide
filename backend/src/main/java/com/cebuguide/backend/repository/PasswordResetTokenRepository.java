package com.cebuguide.backend.repository;

import com.cebuguide.backend.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    void deleteByUserId(Long userId);
    void deleteByExpiresAtBefore(Instant time);
}
