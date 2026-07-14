package com.biblioteca.virtual.controller;

import com.biblioteca.virtual.domain.Usuario;
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

    // Patrón sencillo para validar formato de correo (HU-02 y HU-04)
    private static final String PATRON_CORREO = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

    // ---------- LISTADO + CONSULTA (HU-03) ----------
    @GetMapping("/usuarios")
    public String inicio(
            @RequestParam(value = "identificacion", required = false) String identificacionBuscar,
            @RequestParam(value = "nombre", required = false) String nombreBuscar,
            @RequestParam(value = "correo", required = false) String correoBuscar,
            Model model) {

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

        // Validación de campos obligatorios, común a registro y actualización
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()
                || usuario.getPrimerApellido() == null || usuario.getPrimerApellido().isBlank()
                || usuario.getSegundoApellido() == null || usuario.getSegundoApellido().isBlank()
                || usuario.getCorreoElectronico() == null || usuario.getCorreoElectronico().isBlank()
                || usuario.getTelefono() == null || usuario.getTelefono().isBlank()
                || usuario.getRol() == null) {

            model.addAttribute("mensaje", "Campos obligatorios");
            model.addAttribute("modoEdicion", modoEdicion);
            return "usuario_form";
        }

        if (!usuario.getCorreoElectronico().matches(PATRON_CORREO)) {

            model.addAttribute("mensaje", "Formato de correo inválido");
            model.addAttribute("modoEdicion", modoEdicion);
            return "usuario_form";
        }

        if (!modoEdicion) {

            // ----- HU-02: Registrar usuario -----
            if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()) {

                model.addAttribute("mensaje", "Campos obligatorios");
                model.addAttribute("modoEdicion", false);
                return "usuario_form";
            }

            if (usuarioService.existeIdentificacion(usuario.getIdentificacion())) {

                model.addAttribute("mensaje", "El usuario ya existe");
                model.addAttribute("modoEdicion", false);
                return "usuario_form";
            }

            if (usuarioService.existeCorreo(usuario.getCorreoElectronico())) {

                model.addAttribute("mensaje", "Correo electrónico ya registrado");
                model.addAttribute("modoEdicion", false);
                return "usuario_form";
            }

            usuario.setActivo(true);
            usuarioService.save(usuario);

            redirectAttributes.addFlashAttribute("mensaje", "Usuario registrado satisfactoriamente");
            redirectAttributes.addFlashAttribute("exito", true);

            return "redirect:/usuarios";

        } else {

            // ----- HU-04: Actualizar usuario -----
            Usuario existente = usuarioService.buscarPorIdentificacion(usuario.getIdentificacion());

            if (existente == null) {

                model.addAttribute("mensaje", "No existe el usuario");
                model.addAttribute("modoEdicion", true);
                return "usuario_form";
            }

            if (usuarioService.existeCorreoParaOtroUsuario(usuario.getCorreoElectronico(), existente.getIdentificacion())) {

                model.addAttribute("mensaje", "Correo electrónico ya registrado");
                model.addAttribute("modoEdicion", true);
                return "usuario_form";
            }

            // La identificación no se modifica; la contraseña tampoco se toca aquí
            existente.setNombre(usuario.getNombre());
            existente.setPrimerApellido(usuario.getPrimerApellido());
            existente.setSegundoApellido(usuario.getSegundoApellido());
            existente.setCorreoElectronico(usuario.getCorreoElectronico());
            existente.setTelefono(usuario.getTelefono());
            existente.setRol(usuario.getRol());

            usuarioService.save(existente);

            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado satisfactoriamente");
            redirectAttributes.addFlashAttribute("exito", true);

            return "redirect:/usuarios";
        }
    }

    // ---------- FORMULARIO DE EDICIÓN (HU-04) ----------
    @GetMapping("/usuario/modificar/{identificacion}")
    public String modificar(@PathVariable Long identificacion,
                            Model model) {

        Usuario usuario = usuarioService.buscarPorIdentificacion(identificacion);

        model.addAttribute("usuario", usuario);
        model.addAttribute("modoEdicion", true);

        return "usuario_form";
    }

    // ---------- DESACTIVAR (baja lógica, no elimina el registro) ----------
    @PostMapping("/usuario/desactivar/{identificacion}")
    public String desactivar(@PathVariable Long identificacion,
                             RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioService.buscarPorIdentificacion(identificacion);

        if (usuario != null) {

            usuario.setActivo(false);
            usuarioService.save(usuario);

            redirectAttributes.addFlashAttribute("mensaje", "Usuario desactivado satisfactoriamente");
            redirectAttributes.addFlashAttribute("exito", true);
        }

        return "redirect:/usuarios";
    }

}