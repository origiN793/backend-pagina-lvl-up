package cl.lvlupgamer.servicio;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cl.lvlupgamer.dto.AuthResponse;
import cl.lvlupgamer.dto.LoginRequest;
import cl.lvlupgamer.dto.UsuarioResponse;
import cl.lvlupgamer.error.CredencialesInvalidasException;
import cl.lvlupgamer.error.RecursoNoEncontradoException;
import cl.lvlupgamer.error.TokenInvalidoException;
import cl.lvlupgamer.modelo.Usuario;
import cl.lvlupgamer.repositorio.UsuarioRepository;
import cl.lvlupgamer.seguridad.JwtService;
import io.jsonwebtoken.Claims;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    
    public AuthResponse login(LoginRequest datos) {
        Usuario usuario = usuarios.findByCorreo(datos.correo().trim().toLowerCase())
                .orElseThrow(CredencialesInvalidasException::new);

        if (!usuario.isActivo() || !encoder.matches(datos.password(), usuario.getPassword())) {
            throw new CredencialesInvalidasException();
        }
        return respuesta(usuario);
    }

    
    
    
    public AuthResponse renovar(String refreshToken) {
        Claims datos = jwt.validarRefresh(refreshToken);
        Usuario usuario = usuarios.findByCorreo(datos.getSubject())
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new TokenInvalidoException("La sesión ya no es válida."));
        return respuesta(usuario);
    }

    public UsuarioResponse perfil(String correo) {
        return usuarios.findByCorreo(correo)
                .map(UsuarioResponse::de)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + correo));
    }

    private AuthResponse respuesta(Usuario usuario) {
        return new AuthResponse(jwt.generarAccessToken(usuario), jwt.generarRefreshToken(usuario),
                UsuarioResponse.de(usuario));
    }
}
