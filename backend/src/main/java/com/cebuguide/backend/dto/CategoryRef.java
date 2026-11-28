package com.cebuguide.backend.dto;

import com.cebuguide.backend.model.Category;

public record CategoryRef(Long id, String name, String markerColor) {
    public static CategoryRef from(Category c) {
        return new CategoryRef(c.getId(), c.getName(), c.getMarkerColor());
    }
}
