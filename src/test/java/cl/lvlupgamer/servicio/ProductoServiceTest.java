package cl.lvlupgamer.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.lvlupgamer.dto.ProductoRequest;
import cl.lvlupgamer.dto.ProductoResponse;
import cl.lvlupgamer.error.ConflictoException;
import cl.lvlupgamer.error.DatosInvalidosException;
import cl.lvlupgamer.modelo.Categoria;
import cl.lvlupgamer.modelo.Producto;
import cl.lvlupgamer.repositorio.CategoriaRepository;
import cl.lvlupgamer.repositorio.DetalleBoletaRepository;
import cl.lvlupgamer.repositorio.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productos;
    @Mock
    private CategoriaRepository categorias;
    @Mock
    private DetalleBoletaRepository detalles;

    private ProductoService servicio;

    @BeforeEach
    void preparar() {
        servicio = new ProductoService(productos, categorias, detalles);
    }

    private ProductoRequest solicitud(String codigo, String categoria) {
        return new ProductoRequest(codigo, "Catan", categoria, 29990, 10, 3, "Juego de mesa", "/img.jpg");
    }

    @Test
    void creaUnProductoConSuCategoria() {
        when(productos.existsByCodigo("JM001")).thenReturn(false);
        when(categorias.findByNombre("Juegos de Mesa")).thenReturn(Optional.of(new Categoria("Juegos de Mesa", null)));
        when(productos.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoResponse creado = servicio.crear(solicitud(" JM001 ", "Juegos de Mesa"));

        assertEquals("JM001", creado.codigo());
        assertEquals("Juegos de Mesa", creado.categoria());
        assertEquals(29990, creado.precio());
    }

    @Test
    void unCodigoRepetidoLanzaConflicto() {
        when(productos.existsByCodigo("JM001")).thenReturn(true);

        ConflictoException error = assertThrows(ConflictoException.class,
                () -> servicio.crear(solicitud("JM001", "Juegos de Mesa")));

        assertEquals("codigo", error.getCampo());
        verify(productos, never()).save(any(Producto.class));
    }

    @Test
    void unaCategoriaInexistenteLanzaDatosInvalidos() {
        when(productos.existsByCodigo("JM001")).thenReturn(false);
        when(categorias.findByNombre("Inventada")).thenReturn(Optional.empty());

        DatosInvalidosException error = assertThrows(DatosInvalidosException.class,
                () -> servicio.crear(solicitud("JM001", "Inventada")));

        assertEquals("categoria", error.getCampo());
    }

    @Test
    void noSePuedeEliminarUnProductoQueYaSeVendio() {
        Producto producto = new Producto("JM001", "Catan", new Categoria("Juegos de Mesa", null), 29990, 10, 3, "", "");
        when(productos.findById(1L)).thenReturn(Optional.of(producto));
        when(detalles.existsByProductoId(1L)).thenReturn(true);

        assertThrows(ConflictoException.class, () -> servicio.eliminar(1L));
        verify(productos, never()).delete(any(Producto.class));
    }
}
