package com.cebuguide.backend.service;

import com.cebuguide.backend.dto.UpdateProfileRequest;
import com.cebuguide.backend.exception.DuplicateEmailException;
import com.cebuguide.backend.exception.InvalidCredentialsException;
import com.cebuguide.backend.model.User;
import com.cebuguide.backend.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Profile operations. The user is always taken from the login token, never from the request, so nobody can edit another account. */
@Service
public class ProfileService {

    private final UserRepository users;

    public ProfileService(UserRepository users) {
        this.users = users;
    }

    @Transactional(readOnly = true)
    public User get(Jwt jwt) {
        return load(jwt);
    }

    @Transactional
    public User update(Jwt jwt, UpdateProfileRequest request) {
        User user = load(jwt);
        String email = request.email().trim().toLowerCase();
        users.findByEmailIgnoreCase(email)
                .filter(other -> !other.getId().equals(user.getId()))
                .ifPresent(other -> {
                    throw new DuplicateEmailException();
                });
        user.setName(request.name().trim());
        user.setEmail(email);
        try {
            return users.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException();
        }
    }

    private User load(Jwt jwt) {
        return users.findById(Long.valueOf(jwt.getSubject())).orElseThrow(InvalidCredentialsException::new);
    }
}
