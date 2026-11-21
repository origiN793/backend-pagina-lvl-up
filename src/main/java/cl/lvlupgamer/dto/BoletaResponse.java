package cl.lvlupgamer.dto;

import java.time.LocalDateTime;
import java.util.List;

import cl.lvlupgamer.modelo.Boleta;



public record BoletaResponse(
        Long id,
        LocalDateTime fecha,
        Integer unidades,
        Integer total,
        String cliente,
        String correo,
        List<DetalleBoletaResponse> detalles) {

    public static BoletaResponse de(Boleta b) {
        String cliente = b.getUsuario() == null ? "Sin cliente"
                : b.getUsuario().getNombre() + " " + b.getUsuario().getApellidos();
        String correo = b.getUsuario() == null ? null : b.getUsuario().getCorreo();
        return new BoletaResponse(b.getId(), b.getFecha(), b.getUnidades(), b.getTotal(), cliente, correo,
                b.getDetalles().stream().map(DetalleBoletaResponse::de).toList());
    }
}
