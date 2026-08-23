package com.biblioteca.virtual.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CorreoService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoBienvenida(String destinatario, String nombre, String username, String rawPassword) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom("TU_CORREO@gmail.com"); // El mismo que pusiste en properties
        mensaje.setTo(destinatario);
        mensaje.setSubject("¡Bienvenido a la Biblioteca Virtual! 📚");
        
        String contenido = "Hola " + nombre + ",\n\n"
                + "Tu cuenta en la Biblioteca Virtual ha sido creada exitosamente.\n\n"
                + "Tus credenciales de acceso son:\n"
                + "👤 Usuario: " + username + "\n"
                + "🔑 Contraseña: " + rawPassword + "\n\n"
                + "Por favor, ingresa al sistema para cambiar tu contraseña y empezar a reservar tus libros favoritos.\n\n"
                + "Saludos cordiales,\n"
                + "El equipo de Administración.";
                
        mensaje.setText(contenido);
        mailSender.send(mensaje);
    }
    public void enviarCorreo2FA(String destinatario, String codigo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom("TU_CORREO@gmail.com"); // el mismo correo que ya tenemos configurado en application.properties
        mensaje.setTo(destinatario);
        mensaje.setSubject("Código de Seguridad 2FA - Biblioteca Virtual 🔐");

        String contenido = "Hola Administrador,\n\n"
                + "Se ha detectado un inicio de sesión exitoso con tus credenciales.\n"
                + "Tu código de verificación de un solo uso (OTP) es: " + codigo + "\n\n"
                + "Por favor, ingresa este código en la pantalla del sistema para continuar.\n"
                + "Si no solicitaste este acceso, por favor revisa la seguridad de tu cuenta.\n\n"
                + "Sistema de Biblioteca Virtual.";

        mensaje.setText(contenido);
        mailSender.send(mensaje);
    }
}

