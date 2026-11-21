package cl.lvlupgamer.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;


public record BoletaRequest(
        @NotEmpty(message = "La compra no tiene productos") @Valid List<LineaBoleta> lineas) {
}
