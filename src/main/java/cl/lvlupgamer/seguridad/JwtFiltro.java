package cl.lvlupgamer.seguridad;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import cl.lvlupgamer.error.TokenInvalidoException;
import cl.lvlupgamer.modelo.Usuario;
import cl.lvlupgamer.repositorio.UsuarioRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;








public class JwtFiltro extends OncePerRequestFilter {

    private final JwtService jwt;
    private final UsuarioRepository usuarios;

    public JwtFiltro(JwtService jwt, UsuarioRepository usuarios) {
        this.jwt = jwt;
        this.usuarios = usuarios;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest solicitud, HttpServletResponse respuesta,
                                    FilterChain cadena) throws ServletException, IOException {
        String cabecera = solicitud.getHeader("Authorization");

        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            try {
                Claims datos = jwt.validarAccess(cabecera.substring(7));
                Usuario usuario = usuarios.findByCorreo(datos.getSubject()).orElse(null);

                if (usuario != null && usuario.isActivo()) {
                    
                    var autenticacion = new UsernamePasswordAuthenticationToken(
                            usuario.getCorreo(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())));
                    SecurityContextHolder.getContext().setAuthentication(autenticacion);
                }
            } catch (TokenInvalidoException e) {
                
            }
        }

        cadena.doFilter(solicitud, respuesta);
    }
}
