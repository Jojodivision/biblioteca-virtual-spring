package com.ungatoensarchi.api.dto;

public class CategoryResponseDto {
    private Long id;
    private String slug;
    private String name;
    private String description;
    private String language;

    public CategoryResponseDto() {}

    public CategoryResponseDto(Long id, String slug, String name, String description, String language) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.description = description;
        this.language = language;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}