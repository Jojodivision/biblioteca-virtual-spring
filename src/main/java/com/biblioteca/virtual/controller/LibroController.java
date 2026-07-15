package com.biblioteca.virtual.controller;

import com.biblioteca.virtual.domain.Libro; 
import com.biblioteca.virtual.service.LibroService;
import com.biblioteca.virtual.service.PrestamoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class LibroController {

    @Autowired
    private LibroService libroService;
    
    @Autowired 
    private PrestamoService prestamoService;

    // --- PRIMER MÉTODO: Mostrar la página principal ---
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
    
    // --- RUTA PARA RESERVAR UN LIBRO ---
    @PostMapping("/reservar/{id}")
    public String reservarLibro(@PathVariable("id") Long idLibro, java.security.Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        // Si no hay sesión iniciada, lo mandamos al login
        if (principal == null) {
            return "redirect:/login";
        }
        
        try {
            // Mandamos a llamar tu lógica matemática
            prestamoService.realizarPrestamo(idLibro, principal.getName());
            
            redirectAttributes.addFlashAttribute("mensaje", "¡Libro reservado con éxito! Tienes 7 días para devolverlo.");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            // Si no hay stock o hay un error, mostramos una alerta roja
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/"; // Lo devolvemos al catálogo principal
    }

    // --- RUTA PARA VER EL PERFIL DEL ESTUDIANTE ---
    @GetMapping("/perfil")
    public String verPerfil(org.springframework.ui.Model model, java.security.Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        // Buscamos solo los recibos que le pertenecen a este estudiante
        var prestamos = prestamoService.obtenerPrestamosPorUsername(principal.getName());
        
        model.addAttribute("prestamos", prestamos);
        model.addAttribute("username", principal.getName());
        
        return "perfil"; // Esto buscará el archivo perfil.html
    }
    
    @GetMapping("/login")
    public String login() {
        return "login"; // Busca el archivo login.html 
    }
}