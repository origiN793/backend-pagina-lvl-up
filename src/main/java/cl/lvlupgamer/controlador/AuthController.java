package cl.lvlupgamer.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.lvlupgamer.dto.AuthResponse;
import cl.lvlupgamer.dto.LoginRequest;
import cl.lvlupgamer.dto.RefreshRequest;
import cl.lvlupgamer.dto.RegistroRequest;
import cl.lvlupgamer.dto.UsuarioResponse;
import cl.lvlupgamer.servicio.AuthService;
import cl.lvlupgamer.servicio.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;






@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Login, registro y renovación de tokens JWT")
public class AuthController {

    private final AuthService auth;
    private final UsuarioService usuarios;

    public AuthController(AuthService auth, UsuarioService usuarios) {
        this.auth = auth;
        this.usuarios = usuarios;
    }

    @PostMapping("/login")
    @Operation(summary = "Inicia sesión y entrega los tokens JWT")
    public AuthResponse login(@Valid @RequestBody LoginRequest datos) {
        return auth.login(datos);
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra una cuenta nueva con rol CLIENTE")
    public UsuarioResponse registro(@Valid @RequestBody RegistroRequest datos) {
        return usuarios.registrar(datos);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renueva el access token usando el refresh token")
    public AuthResponse renovar(@Valid @RequestBody RefreshRequest datos) {
        return auth.renovar(datos.refreshToken());
    }

    @GetMapping("/me")
    @Operation(summary = "Datos del usuario con sesión")
    @SecurityRequirement(name = "bearerAuth")
    public UsuarioResponse yo(Authentication authentication) {
        return auth.perfil(authentication.getName());
    }
}
