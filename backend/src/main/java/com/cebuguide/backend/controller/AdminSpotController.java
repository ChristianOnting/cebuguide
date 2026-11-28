package com.cebuguide.backend.controller;

import com.cebuguide.backend.dto.SpotRequest;
import com.cebuguide.backend.dto.SpotResponse;
import com.cebuguide.backend.service.SpotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Spot management for administrators (FR-008 to FR-011 and FR-014). */
@RestController
@RequestMapping("/api/admin/spots")
public class AdminSpotController {

    private final SpotService spotService;

    public AdminSpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @GetMapping
    public List<SpotResponse> list(@RequestParam(name = "q", required = false) String keyword,
                                   @RequestParam(name = "status", defaultValue = "ALL") String status) {
        return spotService.adminList(keyword, status).stream().map(SpotResponse::from).toList();
    }

    @GetMapping("/{id}")
    public SpotResponse get(@PathVariable Long id) {
        return SpotResponse.from(spotService.get(id));
    }

    @PostMapping
    public ResponseEntity<SpotResponse> create(@Valid @RequestBody SpotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(SpotResponse.from(spotService.create(request)));
    }

    @PutMapping("/{id}")
    public SpotResponse update(@PathVariable Long id, @Valid @RequestBody SpotRequest request) {
        return SpotResponse.from(spotService.update(id, request));
    }

    @PostMapping("/{id}/deactivate")
    public SpotResponse deactivate(@PathVariable Long id) {
        return SpotResponse.from(spotService.setActive(id, false));
    }

    @PostMapping("/{id}/reactivate")
    public SpotResponse reactivate(@PathVariable Long id) {
        return SpotResponse.from(spotService.setActive(id, true));
    }
}
