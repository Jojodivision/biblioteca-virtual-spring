package com.biblioteca.virtual.controller;

import com.biblioteca.virtual.domain.Usuario;
import com.biblioteca.virtual.service.CorreoService;
import com.biblioteca.virtual.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private com.biblioteca.virtual.dao.UsuarioDao usuarioDao;

    // Patrón sencillo para validar formato de correo
    private static final String PATRON_CORREO = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
    
    @Autowired
    private org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CorreoService correoService;

    // ---------- LISTADO + CONSULTA (HU-03) ----------
    @GetMapping("/usuarios")
    public String inicio(
            @RequestParam(value = "identificacion", required = false) String identificacionBuscar,
            @RequestParam(value = "nombre", required = false) String nombreBuscar,
            @RequestParam(value = "correo", required = false) String correoBuscar,
            Model model, 
            java.security.Principal principal) { 

        if (principal == null) {
            return "redirect:/login";
        }

        boolean seRealizoBusqueda =
                (identificacionBuscar != null && !identificacionBuscar.isBlank())
                        || (nombreBuscar != null && !nombreBuscar.isBlank())
                        || (correoBuscar != null && !correoBuscar.isBlank());

        List<Usuario> usuarios;

        if (!seRealizoBusqueda) {
            usuarios = usuarioService.getUsuarios();
        } else if (identificacionBuscar != null && !identificacionBuscar.isBlank()) {
            if (!identificacionBuscar.matches("\\d+")) {
                model.addAttribute("mensajeBusqueda", "Información inválida");
                usuarios = usuarioService.getUsuarios();
            } else {
                Usuario encontrado = usuarioService.buscarPorIdentificacion(Long.parseLong(identificacionBuscar));
                if (encontrado == null) {
                    model.addAttribute("mensajeBusqueda", "No existe el usuario");
                    usuarios = List.of();
                } else {
                    usuarios = List.of(encontrado);
                }
            }
        } else if (correoBuscar != null && !correoBuscar.isBlank()) {
            if (!correoBuscar.matches(PATRON_CORREO)) {
                model.addAttribute("mensajeBusqueda", "Información inválida");
                usuarios = usuarioService.getUsuarios();
            } else {
                Usuario encontrado = usuarioService.buscarPorCorreo(correoBuscar);
                if (encontrado == null) {
                    model.addAttribute("mensajeBusqueda", "No existe el usuario");
                    usuarios = List.of();
                } else {
                    usuarios = List.of(encontrado);
                }
            }
        } else {
            usuarios = usuarioService.buscarPorNombre(nombreBuscar);
            if (usuarios.isEmpty()) {
                model.addAttribute("mensajeBusqueda", "No existe el usuario");
            }
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("identificacionBuscar", identificacionBuscar);
        model.addAttribute("nombreBuscar", nombreBuscar);
        model.addAttribute("correoBuscar", correoBuscar);

        return "usuarios";
    }

    // ---------- FORMULARIO DE REGISTRO (HU-02) ----------
    @GetMapping("/usuario/agregar")
    public String agregar(Usuario usuario, Model model) {
        model.addAttribute("modoEdicion", false);
        return "usuario_form";
    }

    // ---------- GUARDAR (HU-02 registrar / HU-04 actualizar) ----------
    @PostMapping("/usuario/guardar")
    public String guardar(@ModelAttribute Usuario usuario,
                          @RequestParam(value = "modoEdicion", defaultValue = "false") boolean modoEdicion,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        if (usuario.getNombre() == null || usuario.getNombre().isBlank()
                || usuario.getPrimerApellido() == null || usuario.getPrimerApellido().isBlank()
                || usuario.getSegundoApellido() == null || usuario.getSegundoApellido().isBlank()
                || usuario.getCorreo() == null || usuario.getCorreo().isBlank()
                || usuario.getTelefono() == null || usuario.getTelefono().isBlank()
                || usuario.getRol() == null) {

            model.addAttribute("mensaje", "Campos obligatorios");
            model.addAttribute("modoEdicion", modoEdicion);
            return "usuario_form";
        }

        if (!usuario.getCorreo().matches(PATRON_CORREO)) {
            model.addAttribute("mensaje", "Formato de correo inválido");
            model.addAttribute("modoEdicion", modoEdicion);
            return "usuario_form";
        }

        if (!modoEdicion) {
            // Lógica para NUEVO usuario
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                model.addAttribute("mensaje", "Campos obligatorios");
                model.addAttribute("modoEdicion", false);
                return "usuario_form";
            }
            if (usuarioService.existeIdentificacion(usuario.getIdentificacion())) {
                model.addAttribute("mensaje", "El usuario ya existe");
                model.addAttribute("modoEdicion", false);
                return "usuario_form";
            }
            if (usuarioService.existeCorreo(usuario.getCorreo())) {
                model.addAttribute("mensaje", "Correo electrónico ya registrado");
                model.addAttribute("modoEdicion", false);
                return "usuario_form";
            }

            // 1. Guardamos la contraseña original en una variable temporal para el correo
            String rawPassword = usuario.getPassword();
            
            // 2. Encriptamos la contraseña para guardarla segura en la BD
            usuario.setPassword(passwordEncoder.encode(rawPassword));
            usuario.setActivo(true);
            
            // 3. Guardamos el usuario
            usuarioService.save(usuario);
            
            // 4. Intentamos enviar el correo de bienvenida
            try {
                correoService.enviarCorreoBienvenida(usuario.getCorreo(), usuario.getNombre(), usuario.getUsername(), rawPassword);
                redirectAttributes.addFlashAttribute("mensaje", "Usuario registrado y correo de bienvenida enviado.");
            } catch (Exception e) {
                log.error("Error enviando correo: " + e.getMessage());
                redirectAttributes.addFlashAttribute("mensaje", "Usuario registrado correctamente (El correo no se pudo enviar).");
            }

            redirectAttributes.addFlashAttribute("exito", true);
            return "redirect:/usuarios";

        } else {
            // Lógica para ACTUALIZAR usuario existente
            Usuario existente = usuarioService.buscarPorIdentificacion(usuario.getIdentificacion());
            if (existente == null) {
                model.addAttribute("mensaje", "No existe el usuario");
                model.addAttribute("modoEdicion", true);
                return "usuario_form";
            }
            if (usuarioService.existeCorreoParaOtroUsuario(usuario.getCorreo(), existente.getIdentificacion())) {
                model.addAttribute("mensaje", "Correo electrónico ya registrado");
                model.addAttribute("modoEdicion", true);
                return "usuario_form";
            }

            existente.setNombre(usuario.getNombre());
            existente.setPrimerApellido(usuario.getPrimerApellido());
            existente.setSegundoApellido(usuario.getSegundoApellido());
            existente.setCorreo(usuario.getCorreo()); 
            existente.setTelefono(usuario.getTelefono());
            existente.setRol(usuario.getRol());

            // Si es edición, no tocamos la contraseña aquí (se hace en otro módulo) ni mandamos correo
            usuarioService.save(existente);
            
            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado satisfactoriamente");
            redirectAttributes.addFlashAttribute("exito", true);
            return "redirect:/usuarios";
        }
    }

    // ---------- FORMULARIO DE EDICIÓN (HU-04) ----------
    @GetMapping("/usuario/modificar/{identificacion}")
    public String modificar(@PathVariable Long identificacion, Model model) {
        Usuario usuario = usuarioService.buscarPorIdentificacion(identificacion);
        model.addAttribute("usuario", usuario);
        model.addAttribute("modoEdicion", true);
        return "usuario_form";
    }

    // ---------- DESACTIVAR (baja lógica, no elimina el registro) ----------
    @PostMapping("/usuario/desactivar/{identificacion}")
    public String desactivar(@PathVariable Long identificacion, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.buscarPorIdentificacion(identificacion);
        if (usuario != null) {
            usuario.setActivo(false);
            usuarioService.save(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario desactivado satisfactoriamente");
            redirectAttributes.addFlashAttribute("exito", true);
        }
        return "redirect:/usuarios";
    }

    // ---------- MÓDULO DE MULTAS Y PENALIZACIONES ----------
    @GetMapping("/multas")
    public String gestionarMultas(Model model, java.security.Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        Usuario usuarioActual = usuarioDao.findByUsername(principal.getName());
        boolean esAdmin = "ROLE_ADMIN".equals(usuarioActual.getRol());
        List<Usuario> morosos;

        if (esAdmin) {
            // Admin ve a todos los deudores
            morosos = usuarioService.getUsuarios().stream()
                    .filter(u -> (u.getMultaPendiente() != null && u.getMultaPendiente() > 0) || (u.getMultaDanos() != null && u.getMultaDanos() > 0))
                    .collect(java.util.stream.Collectors.toList());
        } else {
            // Estudiante solo se ve a sí mismo si debe dinero
            morosos = new java.util.ArrayList<>();
            if ((usuarioActual.getMultaPendiente() != null && usuarioActual.getMultaPendiente() > 0) || (usuarioActual.getMultaDanos() != null && usuarioActual.getMultaDanos() > 0)) {
                morosos.add(usuarioActual);
            }
        }

        model.addAttribute("morosos", morosos);
        model.addAttribute("esAdmin", esAdmin); 
        return "multas";
    }

    @PostMapping("/multas/normalizar/{identificacion}")
    public String normalizarMulta(@PathVariable Long identificacion, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.buscarPorIdentificacion(identificacion);
        
        if (usuario != null) {
            // El estudiante pagó, enceramos ambas deudas (mora y daños)
            usuario.setMultaPendiente(0.0);
            usuario.setMultaDanos(0.0); 
            usuarioService.save(usuario);
            
            redirectAttributes.addFlashAttribute("mensaje", "La cuenta de " + usuario.getNombre() + " ha sido normalizada (Saldo: ₡0). Ya puede realizar préstamos nuevamente.");
            redirectAttributes.addFlashAttribute("tipo", "success");
        }
        
        return "redirect:/multas";
    }
    // ---------- ELIMINACIÓN FÍSICA (Hard Delete) ----------
    @PostMapping("/usuario/eliminar/{identificacion}")
    public String eliminar(@PathVariable Long identificacion, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(identificacion);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado definitivamente de la base de datos.");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            // Si salta un error, es porque MySQL protegió la integridad referencial
            redirectAttributes.addFlashAttribute("mensaje", "No se puede borrar este usuario porque tiene historial de préstamos o multas. Te recomendamos usar la opción de 'Desactivar'.");
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/usuarios";
    }
}
