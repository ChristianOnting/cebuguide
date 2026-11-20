package com.cebuguide.backend.dto;

import java.util.List;

public record AdminDashboardResponse(long totalUsers, long tourists, long administrators, List<UserResponse> recentUsers) {
}
