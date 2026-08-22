package com.biblioteca.virtual.dao;

import com.biblioteca.virtual.domain.Encargo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EncargoDao extends JpaRepository<Encargo, Long> {
    
    // Spring Data JPA detecta la relación y genera el SQL automáticamente
    List<Encargo> findByUsuarioUsername(String username);
    
}
