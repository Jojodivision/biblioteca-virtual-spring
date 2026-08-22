package com.biblioteca.virtual.controller;

import com.biblioteca.virtual.domain.Configuracion;
import com.biblioteca.virtual.service.ConfiguracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ConfiguracionController {

    @Autowired
    private ConfiguracionService configuracionService;
    
    @Autowired
    private com.biblioteca.virtual.dao.UsuarioDao usuarioDao;

    @GetMapping("/configuracion")
    public String verConfiguracion(Model model, java.security.Principal principal) {
        if (principal == null) return "redirect:/login";
        
        // Seguridad: Si un estudiante intenta entrar por URL, lo pateamos al inicio
        com.biblioteca.virtual.domain.Usuario usuario = usuarioDao.findByUsername(principal.getName());
        if (!"ROLE_ADMIN".equals(usuario.getRol())) {
            return "redirect:/";
        }

        model.addAttribute("configuracion", configuracionService.obtenerConfiguracion());
        return "configuracion";
    }

    @PostMapping("/configuracion/guardar")
    public String guardarConfiguracion(Configuracion configuracion, RedirectAttributes redirectAttributes) {
        configuracionService.guardarConfiguracion(configuracion);
        redirectAttributes.addFlashAttribute("mensaje", "¡Reglas de negocio actualizadas con éxito!");
        redirectAttributes.addFlashAttribute("tipo", "success");
        return "redirect:/configuracion";
    }
}
