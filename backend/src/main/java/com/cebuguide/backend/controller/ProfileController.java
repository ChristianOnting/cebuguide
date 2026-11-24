package com.cebuguide.backend.controller;

import com.cebuguide.backend.dto.UpdateProfileRequest;
import com.cebuguide.backend.dto.UserResponse;
import com.cebuguide.backend.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** View and edit the profile of the logged-in user (any role). */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public UserResponse get(@AuthenticationPrincipal Jwt jwt) {
        return UserResponse.from(profileService.get(jwt));
    }

    @PutMapping
    public UserResponse update(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateProfileRequest request) {
        return UserResponse.from(profileService.update(jwt, request));
    }
}
