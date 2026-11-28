package com.cebuguide.backend.service;

import com.cebuguide.backend.dto.CategoryRequest;
import com.cebuguide.backend.dto.CategoryResponse;
import com.cebuguide.backend.exception.ConflictException;
import com.cebuguide.backend.exception.NotFoundException;
import com.cebuguide.backend.model.Category;
import com.cebuguide.backend.repository.CategoryRepository;
import com.cebuguide.backend.repository.SpotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    static final String DEFAULT_COLOR = "#1f4e79";

    private final CategoryRepository categories;
    private final SpotRepository spots;

    public CategoryService(CategoryRepository categories, SpotRepository spots) {
        this.categories = categories;
        this.spots = spots;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return categories.findAllByOrderByNameAsc().stream()
                .map(c -> CategoryResponse.from(c, spots.countByCategoryId(c.getId())))
                .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = request.name().trim();
        if (categories.existsByNameIgnoreCase(name)) {
            throw new ConflictException("name", "A category with this name already exists.");
        }
        Category saved = categories.save(new Category(name, colorOf(request)));
        return CategoryResponse.from(saved, 0);
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = find(id);
        String name = request.name().trim();
        categories.findByNameIgnoreCase(name)
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new ConflictException("name", "A category with this name already exists.");
                });
        category.setName(name);
        category.setMarkerColor(colorOf(request));
        return CategoryResponse.from(categories.save(category), spots.countByCategoryId(id));
    }

    /** A category that still has spots cannot be removed. */
    @Transactional
    public void delete(Long id) {
        Category category = find(id);
        long used = spots.countByCategoryId(id);
        if (used > 0) {
            throw new ConflictException("category", "This category is used by " + used
                    + " spot(s) and cannot be removed. Move or deactivate those spots first.");
        }
        categories.delete(category);
    }

    private Category find(Long id) {
        return categories.findById(id).orElseThrow(() -> new NotFoundException("Category not found."));
    }

    private static String colorOf(CategoryRequest request) {
        return request.markerColor() == null || request.markerColor().isBlank() ? DEFAULT_COLOR : request.markerColor();
    }
}
