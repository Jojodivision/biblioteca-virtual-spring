package com.biblioteca.virtual.controller;

import com.biblioteca.virtual.domain.Libro; 
import com.biblioteca.virtual.service.LibroService;
import com.biblioteca.virtual.service.PrestamoService;
import com.biblioteca.virtual.service.EncargoService; // <-- NUEVO SERVICIO INYECTADO
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class LibroController {

    @Autowired
    private LibroService libroService;
    
    @Autowired 
    private PrestamoService prestamoService;

    @Autowired
    private EncargoService encargoService; // <-- Conectamos el cerebro de los encargos
    
    @Autowired
    private com.biblioteca.virtual.dao.UsuarioDao usuarioDao;

    // --- CATÁLOGO ---
    @GetMapping("/")
    public String inicio(Model model, @RequestParam(value = "palabraClave", required = false) String palabraClave) {
        log.info("Ejecutando el controlador Spring MVC de la Biblioteca");
        java.util.List<Libro> libros;
        
        if (palabraClave != null && !palabraClave.isBlank()) {
            libros = libroService.buscarLibros(palabraClave);
        } else {
            libros = libroService.getLibros();
        }

        model.addAttribute("libros", libros);
        model.addAttribute("palabraClave", palabraClave); 
        return "index";
    }

    @GetMapping("/agregar")
    public String agregar(Libro libro) {
        return "modificar";
    }

    @PostMapping("/guardar")
    public String guardar(Libro libro) {
        libroService.save(libro);
        return "redirect:/";
    }
    
    // --- MOTOR DE RESERVAS ---
    @PostMapping("/reservar/{id}")
    public String reservarLibro(@PathVariable("id") Long idLibro, java.security.Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        
        try {
            prestamoService.realizarPrestamo(idLibro, principal.getName());
            redirectAttributes.addFlashAttribute("mensaje", "¡Libro reservado con éxito! Tienes 7 días para devolverlo.");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/"; 
    }

    @PostMapping("/devolver/{id}")
    public String devolverLibro(@PathVariable("id") Long idPrestamo, java.security.Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        
        try {
            prestamoService.devolverLibro(idPrestamo, principal.getName());
            redirectAttributes.addFlashAttribute("mensaje", "¡Libro devuelto con éxito! Gracias por cuidarlo.");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/prestamos"; 
    }

    // --- PERFIL Y PRÉSTAMOS ---
    @GetMapping("/perfil")
    public String verPerfil(Model model, java.security.Principal principal) {
        if (principal == null) return "redirect:/login";
        com.biblioteca.virtual.domain.Usuario usuario = usuarioDao.findByUsername(principal.getName());
        model.addAttribute("usuario", usuario);
        return "perfil"; 
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(
            @RequestParam("nombre") String nombre,
            @RequestParam("primerApellido") String primerApellido,
            @RequestParam("segundoApellido") String segundoApellido,
            java.security.Principal principal,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";

        try {
            com.biblioteca.virtual.domain.Usuario usuario = usuarioDao.findByUsername(principal.getName());
            usuario.setNombre(nombre);
            usuario.setPrimerApellido(primerApellido);
            usuario.setSegundoApellido(segundoApellido);
            usuarioDao.save(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "¡Tus datos personales han sido actualizados!");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Hubo un error al actualizar el perfil.");
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/perfil";
    }

    @GetMapping("/prestamos")
    public String verPrestamos(Model model, java.security.Principal principal) {
        if (principal == null) return "redirect:/login";
        
        com.biblioteca.virtual.domain.Usuario usuarioActual = usuarioDao.findByUsername(principal.getName());
        boolean esAdmin = "ROLE_ADMIN".equals(usuarioActual.getRol());
        
        java.util.List<com.biblioteca.virtual.domain.Prestamo> prestamos;
        if (esAdmin) {
            prestamos = prestamoService.getPrestamos(); 
        } else {
            prestamos = prestamoService.obtenerPrestamosPorUsername(principal.getName());
        }
        
        model.addAttribute("prestamos", prestamos);
        model.addAttribute("esAdmin", esAdmin); 
        return "prestamos"; 
    }

    // --- NUEVO: MÓDULO DE ENCARGOS ---
    @GetMapping("/encargos")
    public String verEncargos(Model model, java.security.Principal principal) {
        if (principal == null) return "redirect:/login";
        
        com.biblioteca.virtual.domain.Usuario usuarioActual = usuarioDao.findByUsername(principal.getName());
        boolean esAdmin = "ROLE_ADMIN".equals(usuarioActual.getRol());
        
        java.util.List<com.biblioteca.virtual.domain.Encargo> encargos;
        if (esAdmin) {
            encargos = encargoService.getTodosLosEncargos();
        } else {
            encargos = encargoService.getEncargosPorUsuario(principal.getName());
        }
        
        model.addAttribute("encargos", encargos);
        model.addAttribute("esAdmin", esAdmin);
        model.addAttribute("nuevoEncargo", new com.biblioteca.virtual.domain.Encargo()); // Objeto vacío para el form
        
        return "encargos";
    }

    @PostMapping("/encargos/guardar")
    public String guardarEncargo(com.biblioteca.virtual.domain.Encargo encargo, java.security.Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        
        // Asociamos el encargo al estudiante que lo está pidiendo
        com.biblioteca.virtual.domain.Usuario usuarioActual = usuarioDao.findByUsername(principal.getName());
        encargo.setUsuario(usuarioActual);
        
        encargoService.guardarEncargo(encargo);
        
        redirectAttributes.addFlashAttribute("mensaje", "¡Tu solicitud ha sido enviada con éxito! La administración la revisará pronto.");
        redirectAttributes.addFlashAttribute("tipo", "success");
        return "redirect:/encargos";
    }

    @PostMapping("/encargos/actualizar/{id}")
    public String actualizarEstadoEncargo(@PathVariable("id") Long idEncargo, @RequestParam("estado") String estado, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        encargoService.actualizarEstado(idEncargo, estado);
        redirectAttributes.addFlashAttribute("mensaje", "El estado de la solicitud ha sido actualizado a: " + estado);
        redirectAttributes.addFlashAttribute("tipo", "success");
        return "redirect:/encargos";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login"; 
    }
}
