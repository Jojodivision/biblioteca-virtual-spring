package com.biblioteca.virtual.controller;

import com.biblioteca.virtual.domain.Usuario;
import com.biblioteca.virtual.service.CorreoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Random;

@Controller
@RequestMapping("/2fa")
public class DosFAController {

    @Autowired
    private CorreoService correoService;

    @Autowired
    private com.biblioteca.virtual.dao.UsuarioDao usuarioDao;

    @GetMapping("/solicitar")
    public String solicitar2FA(HttpSession session, Principal principal) {
        // si no hay codigo generado en esta sesion, lo creamos
        if (session.getAttribute("codigo_2fa") == null) {
            // generar codigo aleatorio de 6 digitos
            String codigo = String.format("%06d", new Random().nextInt(999999));
            session.setAttribute("codigo_2fa", codigo);

            // buscar el correo actualizado del admin en la BD
            Usuario admin = usuarioDao.findByUsername(principal.getName());

            // enviar el correo
            correoService.enviarCorreo2FA(admin.getCorreo(), codigo);
        }
        return "2fa"; // llama a la vista html
    }

    @PostMapping("/verificar")
    public String verificar2FA(@RequestParam("codigo") String codigo, HttpSession session, Model model) {
        String codigoGuardado = (String) session.getAttribute("codigo_2fa");

        if (codigoGuardado != null && codigoGuardado.equals(codigo)) {
            // si el codigo es correcto, aprobamos la sesion y limpiamos el codigo
            session.setAttribute("2fa_aprobado", true);
            session.removeAttribute("codigo_2fa");
            return "redirect:/usuarios";
        } else {
            // si falla, recargamos la pagina con un error
            model.addAttribute("error", "El código ingresado es incorrecto. Intente de nuevo.");
            return "2fa";
        }
    }
}