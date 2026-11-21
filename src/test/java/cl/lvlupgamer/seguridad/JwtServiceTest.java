package cl.lvlupgamer.seguridad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import cl.lvlupgamer.error.TokenInvalidoException;
import cl.lvlupgamer.modelo.Rol;
import cl.lvlupgamer.modelo.Usuario;
import io.jsonwebtoken.Claims;

class JwtServiceTest {

    private static final String SECRETO = "8+DlAs01t2ogaf72NTgUAnlSKXIM4IBFfg4oVlrBVbE=";

    private final JwtService jwt = new JwtService(SECRETO, 30, 7);

    private Usuario usuario() {
        return new Usuario("191102226", "Ana", "Pérez", "ana@duoc.cl", "hash", Rol.VENDEDOR,
                "Región Metropolitana de Santiago", "Santiago", "Calle 1");
    }

    @Test
    void elAccessTokenGuardaElCorreoYElRol() {
        String token = jwt.generarAccessToken(usuario());

        Claims datos = jwt.validarAccess(token);

        assertEquals("ana@duoc.cl", datos.getSubject());
        assertEquals("VENDEDOR", datos.get("rol", String.class));
    }

    @Test
    void unRefreshTokenNoSirveComoAccessToken() {
        String refresh = jwt.generarRefreshToken(usuario());

        assertThrows(TokenInvalidoException.class, () -> jwt.validarAccess(refresh));
    }

    @Test
    void unAccessTokenNoSirveParaRenovar() {
        String access = jwt.generarAccessToken(usuario());

        assertThrows(TokenInvalidoException.class, () -> jwt.validarRefresh(access));
    }

    @Test
    void unTokenAlteradoSeRechaza() {
        String token = jwt.generarAccessToken(usuario());
        
        String[] partes = token.split("\\.");
        String datos = partes[1];
        String alterado = partes[0] + "." + (datos.charAt(0) == 'A' ? "B" : "A") + datos.substring(1) + "." + partes[2];

        assertThrows(TokenInvalidoException.class, () -> jwt.validarAccess(alterado));
    }

    @Test
    void unTokenVencidoSeRechaza() {
        
        JwtService vencido = new JwtService(SECRETO, -1, 7);
        String token = vencido.generarAccessToken(usuario());

        assertThrows(TokenInvalidoException.class, () -> jwt.validarAccess(token));
    }

    @Test
    void unTokenFirmadoConOtraClaveSeRechaza() {
        String otraClave = java.util.Base64.getEncoder().encodeToString(new byte[32]);
        String ajeno = new JwtService(otraClave, 30, 7).generarAccessToken(usuario());

        assertThrows(TokenInvalidoException.class, () -> jwt.validarAccess(ajeno));
    }
}
