package cl.lvlupgamer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;



public record RegistroRequest(
        @NotBlank(message = "El RUN es obligatorio")
        @Pattern(regexp = "^[0-9]{6,8}[0-9Kk]$", message = "El RUN debe ingresarse sin puntos ni guion. Ejemplo: 19011022K") String run,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre admite hasta 50 caracteres") String nombre,
        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100, message = "El apellido admite hasta 100 caracteres") String apellidos,
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 100, message = "El correo admite hasta 100 caracteres") String correo,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 4, max = 100, message = "La contraseña debe tener entre 4 y 100 caracteres") String password,
        @NotBlank(message = "La región es obligatoria") String region,
        @NotBlank(message = "La comuna es obligatoria") String comuna,
        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 300, message = "La dirección admite hasta 300 caracteres") String direccion) {
}
