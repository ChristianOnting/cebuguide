package com.cebuguide.backend.service;

import com.cebuguide.backend.dto.AuthResponse;
import com.cebuguide.backend.dto.LoginRequest;
import com.cebuguide.backend.dto.RegisterRequest;
import com.cebuguide.backend.dto.UserResponse;
import com.cebuguide.backend.exception.DuplicateEmailException;
import com.cebuguide.backend.exception.InvalidCredentialsException;
import com.cebuguide.backend.model.RevokedToken;
import com.cebuguide.backend.model.Role;
import com.cebuguide.backend.model.User;
import com.cebuguide.backend.repository.RevokedTokenRepository;
import com.cebuguide.backend.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository users;
    private final RevokedTokenRepository revokedTokens;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository users, RevokedTokenRepository revokedTokens,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.revokedTokens = revokedTokens;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** Public registration always creates a TOURIST account (FR-025). */
    @Transactional
    public User register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException();
        }
        User user = new User(request.name().trim(), email, passwordEncoder.encode(request.password()), Role.TOURIST);
        try {
            return users.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException();
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = users.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        JwtService.Token token = jwtService.generate(user);
        return new AuthResponse(token.value(), token.expiresAt(), UserResponse.from(user));
    }

    /** Terminates the session: the token is stored as revoked and rejected from now on. */
    @Transactional
    public void logout(Jwt jwt) {
        revokedTokens.deleteByExpiresAtBefore(Instant.now());
        if (!revokedTokens.existsById(jwt.getId())) {
            revokedTokens.save(new RevokedToken(jwt.getId(), jwt.getExpiresAt()));
        }
    }

    @Transactional(readOnly = true)
    public User currentUser(Jwt jwt) {
        return users.findById(Long.valueOf(jwt.getSubject()))
                .orElseThrow(InvalidCredentialsException::new);
    }
}
