package com.cebuguide.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(name = "uk_categories_name", columnNames = "name"))
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "marker_color", nullable = false, length = 20)
    private String markerColor;

    protected Category() {
    }

    public Category(String name, String markerColor) {
        this.name = name;
        this.markerColor = markerColor;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getMarkerColor() { return markerColor; }

    public void setName(String name) { this.name = name; }
    public void setMarkerColor(String markerColor) { this.markerColor = markerColor; }
}
