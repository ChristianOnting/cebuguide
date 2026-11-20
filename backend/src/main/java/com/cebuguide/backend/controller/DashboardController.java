package com.cebuguide.backend.controller;

import com.cebuguide.backend.dto.AdminDashboardResponse;
import com.cebuguide.backend.dto.TouristDashboardResponse;
import com.cebuguide.backend.dto.UserResponse;
import com.cebuguide.backend.model.Role;
import com.cebuguide.backend.repository.UserRepository;
import com.cebuguide.backend.service.AuthService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Role-specific endpoints. Access is enforced by SecurityConfig (ROLE_TOURIST / ROLE_ADMIN). */
@RestController
@RequestMapping("/api")
public class DashboardController {

    private final UserRepository users;
    private final AuthService authService;

    public DashboardController(UserRepository users, AuthService authService) {
        this.users = users;
        this.authService = authService;
    }

    @GetMapping("/tourist/dashboard")
    public TouristDashboardResponse touristDashboard(@AuthenticationPrincipal Jwt jwt) {
        UserResponse user = UserResponse.from(authService.currentUser(jwt));
        return new TouristDashboardResponse(
                "Welcome, " + user.name() + "!",
                user,
                List.of("View your account profile", "Log in and out securely"),
                List.of("Browse and search Cebu tourist spots", "See spots on an interactive map",
                        "Plan day-by-day itineraries with routes and weather"));
    }

    @GetMapping("/admin/dashboard")
    public AdminDashboardResponse adminDashboard() {
        List<UserResponse> recent = users.findTop5ByOrderByCreatedAtDesc().stream().map(UserResponse::from).toList();
        return new AdminDashboardResponse(users.count(), users.countByRole(Role.TOURIST),
                users.countByRole(Role.ADMIN), recent);
    }

    @GetMapping("/admin/users")
    public List<UserResponse> allUsers() {
        return users.findAllByOrderByCreatedAtDesc().stream().map(UserResponse::from).toList();
    }
}
