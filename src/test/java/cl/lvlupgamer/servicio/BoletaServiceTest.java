package cl.lvlupgamer.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.lvlupgamer.dto.BoletaRequest;
import cl.lvlupgamer.dto.BoletaResponse;
import cl.lvlupgamer.dto.LineaBoleta;
import cl.lvlupgamer.error.RecursoNoEncontradoException;
import cl.lvlupgamer.error.StockInsuficienteException;
import cl.lvlupgamer.modelo.Boleta;
import cl.lvlupgamer.modelo.Categoria;
import cl.lvlupgamer.modelo.Producto;
import cl.lvlupgamer.modelo.Rol;
import cl.lvlupgamer.modelo.Usuario;
import cl.lvlupgamer.repositorio.BoletaRepository;
import cl.lvlupgamer.repositorio.ProductoRepository;
import cl.lvlupgamer.repositorio.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class BoletaServiceTest {

    @Mock
    private ProductoRepository productos;
    @Mock
    private UsuarioRepository usuarios;
    @Mock
    private BoletaRepository boletas;

    private BoletaService servicio;
    private Usuario cliente;
    private Producto catan;

    @BeforeEach
    void preparar() {
        servicio = new BoletaService(productos, usuarios, boletas);

        cliente = new Usuario("205931189", "Fernanda", "López", "fernanda@duoc.cl", "hash", Rol.CLIENTE,
                "Región de Valparaíso", "Viña del Mar", "Pasaje 1");

        catan = new Producto("JM001", "Catan", new Categoria("Juegos de Mesa", null), 29990, 10, 3, "", "/img.jpg");
        catan.setId(1L);
    }

    @Test
    void laCompraDescuentaElStockYCalculaElTotalConElPrecioDelServidor() {
        when(usuarios.findByCorreo("fernanda@duoc.cl")).thenReturn(Optional.of(cliente));
        when(productos.findById(1L)).thenReturn(Optional.of(catan));
        when(boletas.save(any(Boleta.class))).thenAnswer(inv -> inv.getArgument(0));

        BoletaResponse boleta = servicio.crear(new BoletaRequest(List.of(new LineaBoleta(1L, 2))), "fernanda@duoc.cl");

        assertEquals(8, catan.getStock());
        assertEquals(2, boleta.unidades());
        assertEquals(59980, boleta.total());
        assertEquals("Fernanda López", boleta.cliente());
        assertEquals(1, boleta.detalles().size());
        assertEquals(29990, boleta.detalles().get(0).precioUnitario());
    }

    @Test
    void sinStockSuficienteLanzaConflictoYNoGuardaNada() {
        when(usuarios.findByCorreo("fernanda@duoc.cl")).thenReturn(Optional.of(cliente));
        when(productos.findById(1L)).thenReturn(Optional.of(catan));

        BoletaRequest pedido = new BoletaRequest(List.of(new LineaBoleta(1L, 11)));

        assertThrows(StockInsuficienteException.class, () -> servicio.crear(pedido, "fernanda@duoc.cl"));
        assertEquals(10, catan.getStock());
        verify(boletas, never()).save(any(Boleta.class));
    }

    @Test
    void unProductoInexistenteLanzaNoEncontrado() {
        when(usuarios.findByCorreo("fernanda@duoc.cl")).thenReturn(Optional.of(cliente));
        when(productos.findById(99L)).thenReturn(Optional.empty());

        BoletaRequest pedido = new BoletaRequest(List.of(new LineaBoleta(99L, 1)));

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.crear(pedido, "fernanda@duoc.cl"));
        verify(boletas, never()).save(any(Boleta.class));
    }

    @Test
    void unaBoletaInexistenteLanzaNoEncontrado() {
        when(boletas.findById(5L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.buscar(5L));
    }
}
