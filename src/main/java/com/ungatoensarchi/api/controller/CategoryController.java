package com.ungatoensarchi.api.controller;

import com.ungatoensarchi.api.dto.CategoryResponseDto;
import com.ungatoensarchi.api.model.Category;
import com.ungatoensarchi.api.model.CategoryTranslation;
import com.ungatoensarchi.api.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<CategoryResponseDto> getAllCategories(Locale locale) {
        String lang = locale.getLanguage();

        return categoryRepository.findAll().stream()
                .map(cat -> mapToLocalizedDto(cat, lang))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        if (category.getTranslations() != null) {
            category.getTranslations().forEach(t -> t.setCategory(category));
        }
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    private CategoryResponseDto mapToLocalizedDto(Category category, String lang) {
        CategoryTranslation translation = category.getTranslations().stream()
                .filter(t -> t.getLanguageCode().equalsIgnoreCase(lang))
                .findFirst()
                .orElseGet(() -> category.getTranslations().stream()
                        .filter(t -> t.getLanguageCode().equalsIgnoreCase("es"))
                        .findFirst()
                        .orElse(new CategoryTranslation()));

        return new CategoryResponseDto(
                category.getId(),
                category.getSlug(),
                translation.getName() != null ? translation.getName() : category.getSlug(),
                translation.getDescription(),
                translation.getLanguageCode()
        );
    }
}
