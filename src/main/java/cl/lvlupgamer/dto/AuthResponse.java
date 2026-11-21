package cl.lvlupgamer.dto;




public record AuthResponse(
        String accessToken,
        String refreshToken,
        UsuarioResponse usuario) {
}
