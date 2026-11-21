package cl.lvlupgamer.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import cl.lvlupgamer.dto.AuthResponse;
import cl.lvlupgamer.dto.LoginRequest;
import cl.lvlupgamer.error.CredencialesInvalidasException;
import cl.lvlupgamer.error.TokenInvalidoException;
import cl.lvlupgamer.modelo.Rol;
import cl.lvlupgamer.modelo.Usuario;
import cl.lvlupgamer.repositorio.UsuarioRepository;
import cl.lvlupgamer.seguridad.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarios;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtService jwt = new JwtService("8+DlAs01t2ogaf72NTgUAnlSKXIM4IBFfg4oVlrBVbE=", 30, 7);
    private AuthService servicio;
    private Usuario ana;

    @BeforeEach
    void preparar() {
        servicio = new AuthService(usuarios, encoder, jwt);
        ana = new Usuario("191102226", "Ana", "Pérez", "ana@duoc.cl", encoder.encode("clave123"), Rol.ADMINISTRADOR,
                "Región Metropolitana de Santiago", "Santiago", "Calle 1");
    }

    @Test
    void conCredencialesCorrectasEntregaLosDosTokens() {
        when(usuarios.findByCorreo("ana@duoc.cl")).thenReturn(Optional.of(ana));

        AuthResponse respuesta = servicio.login(new LoginRequest("ana@duoc.cl", "clave123"));

        assertEquals("ana@duoc.cl", jwt.validarAccess(respuesta.accessToken()).getSubject());
        assertEquals("ana@duoc.cl", jwt.validarRefresh(respuesta.refreshToken()).getSubject());
        assertEquals(Rol.ADMINISTRADOR, respuesta.usuario().rol());
    }

    @Test
    void unaClaveIncorrectaSeRechaza() {
        when(usuarios.findByCorreo("ana@duoc.cl")).thenReturn(Optional.of(ana));

        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.login(new LoginRequest("ana@duoc.cl", "otra")));
    }

    @Test
    void unCorreoInexistenteSeRechazaConElMismoError() {
        when(usuarios.findByCorreo("nadie@duoc.cl")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.login(new LoginRequest("nadie@duoc.cl", "clave123")));
    }

    @Test
    void unUsuarioDesactivadoNoPuedeIniciarSesion() {
        ana.setActivo(false);
        when(usuarios.findByCorreo("ana@duoc.cl")).thenReturn(Optional.of(ana));

        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.login(new LoginRequest("ana@duoc.cl", "clave123")));
    }

    @Test
    void elRefreshTokenEntregaTokensNuevos() {
        when(usuarios.findByCorreo("ana@duoc.cl")).thenReturn(Optional.of(ana));
        String refresh = jwt.generarRefreshToken(ana);

        AuthResponse nueva = servicio.renovar(refresh);

        assertEquals("ana@duoc.cl", jwt.validarAccess(nueva.accessToken()).getSubject());
    }

    @Test
    void unAccessTokenNoSirveParaRenovar() {
        String access = jwt.generarAccessToken(ana);

        assertThrows(TokenInvalidoException.class, () -> servicio.renovar(access));
    }

    @Test
    void siElUsuarioFueDesactivadoLaRenovacionFalla() {
        String refresh = jwt.generarRefreshToken(ana);
        ana.setActivo(false);
        when(usuarios.findByCorreo("ana@duoc.cl")).thenReturn(Optional.of(ana));

        assertThrows(TokenInvalidoException.class, () -> servicio.renovar(refresh));
    }
}
