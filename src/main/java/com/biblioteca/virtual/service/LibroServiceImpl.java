package com.biblioteca.virtual.service;

import com.biblioteca.virtual.dao.LibroDao;
import com.biblioteca.virtual.domain.Libro;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibroServiceImpl implements LibroService {

    // Inyectamos el DAO que creaste anteriormente
    @Autowired
    private LibroDao libroDao;

    @Override
    @Transactional(readOnly = true)
    public List<Libro> getLibros() {
        return (List<Libro>) libroDao.findAll();
    }

    @Override
    @Transactional
    public void save(Libro libro) {
        libroDao.save(libro);
    }

    @Override
    @Transactional
    public void delete(Libro libro) {
        libroDao.delete(libro);
    }

    @Override
    @Transactional(readOnly = true)
    public Libro getLibro(Libro libro) {
        return libroDao.findById(libro.getIdLibro()).orElse(null);
    }
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public java.util.List<Libro> buscarLibros(String termino) {
        // Le pasamos la palabra clave a ambos campos (título y autor)
        return libroDao.findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase(termino, termino);
    }
}