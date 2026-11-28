package com.cebuguide.backend.dto;

import com.cebuguide.backend.model.Category;

public record CategoryResponse(Long id, String name, String markerColor, long spotCount) {
    public static CategoryResponse from(Category c, long spotCount) {
        return new CategoryResponse(c.getId(), c.getName(), c.getMarkerColor(), spotCount);
    }
}
