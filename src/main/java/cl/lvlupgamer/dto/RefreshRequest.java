package cl.lvlupgamer.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "Falta el token de renovación") String refreshToken) {
}
