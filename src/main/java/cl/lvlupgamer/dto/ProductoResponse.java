package cl.lvlupgamer.dto;

import cl.lvlupgamer.modelo.Producto;



public record ProductoResponse(
        Long id,
        String codigo,
        String nombre,
        String categoria,
        Integer precio,
        Integer stock,
        Integer stockCritico,
        String descripcion,
        String imagen) {

    public static ProductoResponse de(Producto p) {
        return new ProductoResponse(p.getId(), p.getCodigo(), p.getNombre(), p.getCategoria().getNombre(),
                p.getPrecio(), p.getStock(), p.getStockCritico(), p.getDescripcion(), p.getImagen());
    }
}
