package com.biblioteca.virtual.controller;

import com.biblioteca.virtual.domain.Libro; 
import com.biblioteca.virtual.service.LibroService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class LibroController {

    @Autowired
    private LibroService libroService;

    // --- PRIMER MÉTODO: Cargar la página principal ---
    @GetMapping("/")
    public String inicio(Model model) {
        log.info("Ejecutando el controlador Spring MVC de la Biblioteca");
        
        // Obtenemos la lista de libros desde la base de datos
        var libros = libroService.getLibros();

        // Compartimos la lista con la vista HTML bajo el nombre "libros"
        model.addAttribute("libros", libros);

        // Retornamos el nombre del archivo HTML (index.html)
        return "index";
    }

    // --- SEGUNDO MÉTODO: Mostrar el formulario vacío ---
    @GetMapping("/agregar")
    public String agregar(Libro libro) {
        // Spring Boot automáticamente inyecta un objeto Libro vacío en el modelo
        return "modificar";
    }

    // --- TERCER MÉTODO: Atrapar los datos del formulario y guardarlos ---
    @PostMapping("/guardar")
    public String guardar(Libro libro) {
        // Le pasamos el libro lleno con los datos del formulario al servicio
        libroService.save(libro);
        
        // Redirigimos a la página principal para ver la tabla actualizada
        return "redirect:/";
    }
}