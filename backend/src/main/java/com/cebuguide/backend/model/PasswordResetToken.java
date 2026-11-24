package com.cebuguide.backend.model;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * A password reset request. Only the SHA-256 hash of the token is stored, so a copy of the database
 * cannot be used to reset passwords. A token expires after a short time and is deleted when it is used.
 */
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PasswordResetToken() {
    }

    public PasswordResetToken(String tokenHash, User user, Instant expiresAt) {
        this.tokenHash = tokenHash;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public boolean isUsable(Instant now) {
        return expiresAt.isAfter(now);
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Instant getExpiresAt() { return expiresAt; }
}
