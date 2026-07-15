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

    // --- PRIMER MÉTODO: Mostrar la página principal y el buscador ---
    @GetMapping("/")
    public String inicio(Model model, @org.springframework.web.bind.annotation.RequestParam(value = "palabraClave", required = false) String palabraClave) {
        log.info("Ejecutando el controlador Spring MVC de la Biblioteca");
        
        java.util.List<Libro> libros;
        
        // Si el usuario escribió algo en el buscador, usamos tu nuevo método
        if (palabraClave != null && !palabraClave.isBlank()) {
            libros = libroService.buscarLibros(palabraClave);
        } else {
            // Si la barra está vacía, mostramos todo el catálogo
            libros = libroService.getLibros();
        }

        // Compartimos los datos con el HTML
        model.addAttribute("libros", libros);
        model.addAttribute("palabraClave", palabraClave); // Para que el texto no se borre de la barra al buscar

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
    // --- RUTA PARA DEVOLVER UN LIBRO ---
    @PostMapping("/devolver/{id}")
    public String devolverLibro(@PathVariable("id") Long idPrestamo, java.security.Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        try {
            prestamoService.devolverLibro(idPrestamo, principal.getName());
            
            redirectAttributes.addFlashAttribute("mensaje", "¡Libro devuelto con éxito! Gracias por cuidarlo.");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        
        // Lo regresamos a su perfil para que vea el cambio
        return "redirect:/perfil"; 
    }
}