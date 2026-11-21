package cl.lvlupgamer.seguridad;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;





@Component
public class RespuestasSeguridad implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest solicitud, HttpServletResponse respuesta,
                         AuthenticationException ex) throws IOException {
        escribir(respuesta, HttpServletResponse.SC_UNAUTHORIZED,
                "Debes iniciar sesión para acceder a este recurso.");
    }

    @Override
    public void handle(HttpServletRequest solicitud, HttpServletResponse respuesta,
                       AccessDeniedException ex) throws IOException {
        escribir(respuesta, HttpServletResponse.SC_FORBIDDEN,
                "No tienes permisos para realizar esta acción.");
    }

    private void escribir(HttpServletResponse respuesta, int estado, String mensaje) throws IOException {
        respuesta.setStatus(estado);
        respuesta.setContentType("application/json");
        respuesta.setCharacterEncoding("UTF-8");
        respuesta.getWriter().write(
                "{\"status\":" + estado + ",\"mensaje\":\"" + mensaje + "\",\"campos\":{}}");
    }
}
