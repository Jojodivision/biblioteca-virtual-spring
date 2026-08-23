package com.biblioteca.virtual.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
public class DosFAInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // dejamos pasar estaticos y las rutas de login/2fa para no causar bucle infinito
        if (uri.startsWith("/css") || uri.startsWith("/js") || uri.startsWith("/login") || uri.startsWith("/2fa") || uri.startsWith("/error")) {
            return true;
        }

        // revisamos si ya paso por el login de contraseñas
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {

            // revisamos si tiene el rol de admin
            boolean esAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (esAdmin) {
                HttpSession session = request.getSession();

                // si es admin y no tiene el 2fa_aprobado, lo mandamos a validar
                if (session.getAttribute("2fa_aprobado") == null) {
                    response.sendRedirect("/2fa/solicitar");
                    return false;
                }
            }
        }
        return true; // si es estudiante, o si el admin ya paso el 2fa, sigue de largo
    }
}