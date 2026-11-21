package cl.lvlupgamer.dto;

import cl.lvlupgamer.modelo.Rol;
import cl.lvlupgamer.modelo.Usuario;


public record UsuarioResponse(
        Long id,
        String run,
        String nombre,
        String apellidos,
        String correo,
        Rol rol,
        String region,
        String comuna,
        String direccion,
        boolean activo) {

    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getRun(), u.getNombre(), u.getApellidos(), u.getCorreo(),
                u.getRol(), u.getRegion(), u.getComuna(), u.getDireccion(), u.isActivo());
    }
}
