package cl.lvlupgamer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;



public record ProductoRequest(
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 20, message = "El código admite hasta 20 caracteres") String codigo,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre admite hasta 100 caracteres") String nombre,
        @NotBlank(message = "La categoría es obligatoria") String categoria,
        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor que cero") Integer precio,
        @NotNull(message = "El stock es obligatorio")
        @PositiveOrZero(message = "El stock no puede ser negativo") Integer stock,
        @NotNull(message = "El stock crítico es obligatorio")
        @PositiveOrZero(message = "El stock crítico no puede ser negativo") Integer stockCritico,
        @Size(max = 500, message = "La descripción admite hasta 500 caracteres") String descripcion,
        @Size(max = 200, message = "La ruta de la imagen admite hasta 200 caracteres") String imagen) {
}
