package com.ungatoensarchi.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String slug;

    private boolean active = true;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CategoryTranslation> translations = new ArrayList<>();

    public Category() {}

    public Category(Long id, String slug, boolean active, List<CategoryTranslation> translations) {
        this.id = id;
        this.slug = slug;
        this.active = active;
        this.translations = translations;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<CategoryTranslation> getTranslations() { return translations; }
    public void setTranslations(List<CategoryTranslation> translations) { this.translations = translations; }
}