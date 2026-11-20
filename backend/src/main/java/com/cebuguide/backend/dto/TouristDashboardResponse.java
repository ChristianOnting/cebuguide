package com.cebuguide.backend.dto;

import java.util.List;

public record TouristDashboardResponse(String greeting, UserResponse user, List<String> availableNow, List<String> comingNext) {
}
