package cl.lvlupgamer.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.lvlupgamer.dto.ProductoRequest;
import cl.lvlupgamer.dto.ProductoResponse;
import cl.lvlupgamer.servicio.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;










@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Catálogo de productos")
public class ProductoController {

    private final ProductoService servicio;

    public ProductoController(ProductoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Lista los productos, con filtro opcional por categoría")
    public List<ProductoResponse> listar(@RequestParam(required = false) String categoria) {
        return servicio.listar(categoria);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un producto por su id")
    public ProductoResponse obtener(@PathVariable Long id) {
        return servicio.buscar(id);
    }

    
    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Obtiene un producto por su código")
    public ProductoResponse obtenerPorCodigo(@PathVariable String codigo) {
        return servicio.buscarPorCodigo(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un producto (solo administrador)")
    @SecurityRequirement(name = "bearerAuth")
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest producto) {
        return servicio.crear(producto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un producto (solo administrador)")
    @SecurityRequirement(name = "bearerAuth")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest producto) {
        return servicio.actualizar(id, producto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina un producto (solo administrador)")
    @SecurityRequirement(name = "bearerAuth")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
