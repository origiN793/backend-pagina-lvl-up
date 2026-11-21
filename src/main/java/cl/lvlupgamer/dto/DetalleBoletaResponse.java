package cl.lvlupgamer.dto;

import cl.lvlupgamer.modelo.DetalleBoleta;

public record DetalleBoletaResponse(
        Long productoId,
        String codigo,
        String nombre,
        Integer cantidad,
        Integer precioUnitario,
        Integer subtotal) {

    public static DetalleBoletaResponse de(DetalleBoleta d) {
        return new DetalleBoletaResponse(d.getProducto().getId(), d.getProducto().getCodigo(),
                d.getProducto().getNombre(), d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal());
    }
}
