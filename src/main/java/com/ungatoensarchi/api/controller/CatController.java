package com.ungatoensarchi.api.controller;

import com.ungatoensarchi.api.model.Cat;
import com.ungatoensarchi.api.repository.CatRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cats")
@CrossOrigin(origins = "*")
public class CatController {

    private final CatRepository catRepository;

    public CatController(CatRepository catRepository) {
        this.catRepository = catRepository;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("UngatoenSarchi API is online and running!");
    }

    @GetMapping
    public List<Cat> getAllCats() {
        return catRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cat> getCatById(@PathVariable Long id) {
        return catRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cat> createCat(@RequestBody Cat cat) {
        Cat savedCat = catRepository.save(cat);
        return ResponseEntity.ok(savedCat);
    }
}
