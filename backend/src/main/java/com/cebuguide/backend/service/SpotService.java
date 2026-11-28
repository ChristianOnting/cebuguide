package com.cebuguide.backend.service;

import com.cebuguide.backend.dto.SpotRequest;
import com.cebuguide.backend.exception.FieldValidationException;
import com.cebuguide.backend.exception.NotFoundException;
import com.cebuguide.backend.model.Category;
import com.cebuguide.backend.model.Spot;
import com.cebuguide.backend.repository.CategoryRepository;
import com.cebuguide.backend.repository.SpotRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class SpotService {

    private static final Set<String> STATUSES = Set.of("ALL", "ACTIVE", "INACTIVE");

    private final SpotRepository spots;
    private final CategoryRepository categories;
    private final BigDecimal minLatitude;
    private final BigDecimal maxLatitude;
    private final BigDecimal minLongitude;
    private final BigDecimal maxLongitude;

    public SpotService(SpotRepository spots, CategoryRepository categories,
                       @Value("${app.spots.min-latitude:9.4}") BigDecimal minLatitude,
                       @Value("${app.spots.max-latitude:11.4}") BigDecimal maxLatitude,
                       @Value("${app.spots.min-longitude:123.2}") BigDecimal minLongitude,
                       @Value("${app.spots.max-longitude:124.5}") BigDecimal maxLongitude) {
        this.spots = spots;
        this.categories = categories;
        this.minLatitude = minLatitude;
        this.maxLatitude = maxLatitude;
        this.minLongitude = minLongitude;
        this.maxLongitude = maxLongitude;
    }

    /** All spots (active and inactive) for the administrator, optionally filtered. */
    @Transactional(readOnly = true)
    public List<Spot> adminList(String keyword, String status) {
        String k = keyword == null ? "" : keyword.trim();
        String s = status == null ? "ALL" : status.trim().toUpperCase(Locale.ROOT);
        return spots.adminSearch(k, STATUSES.contains(s) ? s : "ALL");
    }

    @Transactional(readOnly = true)
    public Spot get(Long id) {
        return find(id);
    }

    @Transactional
    public Spot create(SpotRequest request) {
        Spot spot = new Spot();
        apply(spot, request);
        return spots.save(spot);
    }

    @Transactional
    public Spot update(Long id, SpotRequest request) {
        Spot spot = find(id);
        apply(spot, request);
        return spots.save(spot);
    }

    /** Deactivating hides the spot from tourists but keeps it, so saved itineraries stay valid. */
    @Transactional
    public Spot setActive(Long id, boolean active) {
        Spot spot = find(id);
        spot.setActive(active);
        return spots.save(spot);
    }

    private void apply(Spot spot, SpotRequest request) {
        requireRange("latitude", "Latitude", request.latitude(), minLatitude, maxLatitude);
        requireRange("longitude", "Longitude", request.longitude(), minLongitude, maxLongitude);
        Category category = categories.findById(request.categoryId())
                .orElseThrow(() -> new FieldValidationException("categoryId", "Choose a valid category."));
        spot.setCategory(category);
        spot.setName(request.name().trim());
        spot.setDescription(request.description().trim());
        spot.setAddress(request.address().trim());
        spot.setLatitude(request.latitude().setScale(6, RoundingMode.HALF_UP));
        spot.setLongitude(request.longitude().setScale(6, RoundingMode.HALF_UP));
        spot.setIndoor(Boolean.TRUE.equals(request.indoor()));
    }

    private static void requireRange(String field, String label, BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            throw new FieldValidationException(field,
                    label + " must be between " + min.toPlainString() + " and " + max.toPlainString() + " (Cebu Province).");
        }
    }

    private Spot find(Long id) {
        return spots.findById(id).orElseThrow(() -> new NotFoundException("Spot not found."));
    }
}
