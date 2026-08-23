package com.biblioteca.virtual.controller;

import com.biblioteca.virtual.dao.UsuarioDao;
import com.biblioteca.virtual.service.CargaMasivaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Controller
@RequestMapping("/admin/libros")
public class CargaMasivaController {

    @Autowired
    private CargaMasivaService cargaMasivaService;

    @Autowired
    private UsuarioDao usuarioDao;

    @PostMapping("/cargar-csv")
    public String cargarLibrosPorLote(@RequestParam("archivo") MultipartFile archivo, RedirectAttributes redirectAttributes, Principal principal) {

        if (principal == null) return "redirect:/login";
        com.biblioteca.virtual.domain.Usuario usuario = usuarioDao.findByUsername(principal.getName());
        if (usuario == null || !"ROLE_ADMIN".equals(usuario.getRol())) {
            return "redirect:/";
        }

        // validar que el archivo no venga vacio
        if (archivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: El archivo adjunto está vacío.");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/";
        }

        // regla de negocio y seguridad: validar extension y tipo mime (rechazar word, jpg, etc)
        String nombreArchivo = archivo.getOriginalFilename();
        String contentType = archivo.getContentType();

        if (nombreArchivo == null || !nombreArchivo.toLowerCase().endsWith(".csv") ||
           (contentType != null && !contentType.equals("text/csv") && !contentType.equals("application/vnd.ms-excel"))) {

            redirectAttributes.addFlashAttribute("mensaje", "Acción denegada: El sistema solo permite la carga de archivos estructurados en formato CSV (.csv). Se detectó un formato no válido.");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/";
        }

        // procesar el archivo si supero los filtros
        try {
            int totalGuardados = cargaMasivaService.procesarArchivoCsv(archivo);
            redirectAttributes.addFlashAttribute("mensaje", "¡Éxito! Se han importado " + totalGuardados + " libros nuevos al catálogo general.");
            redirectAttributes.addFlashAttribute("tipo", "success");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }

        return "redirect:/";
    }
}