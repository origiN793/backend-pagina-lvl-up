package cl.lvlupgamer.seguridad;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cl.lvlupgamer.error.TokenInvalidoException;
import cl.lvlupgamer.modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;










@Service
public class JwtService {

    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";

    private final SecretKey clave;
    private final long accessMs;
    private final long refreshMs;

    public JwtService(@Value("${app.jwt.secreto}") String secretoBase64,
                      @Value("${app.jwt.access-minutos}") long accessMinutos,
                      @Value("${app.jwt.refresh-dias}") long refreshDias) {
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoBase64));
        this.accessMs = accessMinutos * 60_000L;
        this.refreshMs = refreshDias * 86_400_000L;
    }

    public String generarAccessToken(Usuario usuario) {
        return generar(usuario, ACCESS, accessMs);
    }

    public String generarRefreshToken(Usuario usuario) {
        return generar(usuario, REFRESH, refreshMs);
    }

    
    public Claims validarAccess(String token) {
        return validar(token, ACCESS);
    }

    
    public Claims validarRefresh(String token) {
        return validar(token, REFRESH);
    }

    private String generar(Usuario usuario, String tipo, long duracionMs) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(usuario.getCorreo())
                .claim("rol", usuario.getRol().name())
                .claim("tipo", tipo)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + duracionMs))
                .signWith(clave)
                .compact();
    }

    private Claims validar(String token, String tipoEsperado) {
        Claims datos;
        try {
            
            datos = Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenInvalidoException("El token es inválido o ya venció.");
        }
        
        if (!tipoEsperado.equals(datos.get("tipo", String.class))) {
            throw new TokenInvalidoException("El tipo de token no corresponde.");
        }
        return datos;
    }
}
