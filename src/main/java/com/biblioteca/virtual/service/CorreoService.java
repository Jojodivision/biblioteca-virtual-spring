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
}