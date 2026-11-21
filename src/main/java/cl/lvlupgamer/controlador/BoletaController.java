package cl.lvlupgamer.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.lvlupgamer.dto.BoletaRequest;
import cl.lvlupgamer.dto.BoletaResponse;
import cl.lvlupgamer.servicio.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;






@RestController
@RequestMapping("/api/v1/boletas")
@Tag(name = "Boletas", description = "Compras realizadas en la tienda")
@SecurityRequirement(name = "bearerAuth")
public class BoletaController {

    private final BoletaService servicio;

    public BoletaController(BoletaService servicio) {
        this.servicio = servicio;
    }

    
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra una compra y descuenta el stock")
    public BoletaResponse crear(@Valid @RequestBody BoletaRequest solicitud, Authentication authentication) {
        return servicio.crear(solicitud, authentication.getName());
    }

    @GetMapping
    @Operation(summary = "Lista todas las boletas (administrador y vendedor)")
    public List<BoletaResponse> listar() {
        return servicio.listar();
    }

    @GetMapping("/mias")
    @Operation(summary = "Lista las boletas del usuario con sesión")
    public List<BoletaResponse> misBoletas(Authentication authentication) {
        return servicio.listarDe(authentication.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de una boleta (administrador y vendedor)")
    public BoletaResponse obtener(@PathVariable Long id) {
        return servicio.buscar(id);
    }
}
