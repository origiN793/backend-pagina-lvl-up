package cl.lvlupgamer.servicio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.lvlupgamer.dto.BoletaRequest;
import cl.lvlupgamer.dto.BoletaResponse;
import cl.lvlupgamer.dto.LineaBoleta;
import cl.lvlupgamer.error.RecursoNoEncontradoException;
import cl.lvlupgamer.error.StockInsuficienteException;
import cl.lvlupgamer.modelo.Boleta;
import cl.lvlupgamer.modelo.DetalleBoleta;
import cl.lvlupgamer.modelo.Producto;
import cl.lvlupgamer.modelo.Usuario;
import cl.lvlupgamer.repositorio.BoletaRepository;
import cl.lvlupgamer.repositorio.ProductoRepository;
import cl.lvlupgamer.repositorio.UsuarioRepository;

@Service
public class BoletaService {

    private final ProductoRepository productos;
    private final UsuarioRepository usuarios;
    private final BoletaRepository boletas;

    public BoletaService(ProductoRepository productos, UsuarioRepository usuarios, BoletaRepository boletas) {
        this.productos = productos;
        this.usuarios = usuarios;
        this.boletas = boletas;
    }

    
    
    
    @Transactional
    public BoletaResponse crear(BoletaRequest solicitud, String correoCliente) {
        Usuario usuario = usuarios.findByCorreo(correoCliente)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + correoCliente));

        Boleta boleta = new Boleta(LocalDateTime.now(), usuario);
        int unidades = 0;
        int total = 0;

        for (LineaBoleta linea : solicitud.lineas()) {
            Producto producto = productos.findById(linea.productoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe el producto " + linea.productoId()));

            if (producto.getStock() < linea.cantidad()) {
                throw new StockInsuficienteException("No hay stock suficiente de «" + producto.getNombre()
                        + "»: quedan " + producto.getStock() + " unidades.");
            }

            producto.setStock(producto.getStock() - linea.cantidad());
            
            int subtotal = producto.getPrecio() * linea.cantidad();
            boleta.agregarDetalle(new DetalleBoleta(producto, linea.cantidad(), producto.getPrecio(), subtotal));
            unidades += linea.cantidad();
            total += subtotal;
        }

        boleta.setUnidades(unidades);
        boleta.setTotal(total);
        return BoletaResponse.de(boletas.save(boleta));
    }

    
    @Transactional(readOnly = true)
    public List<BoletaResponse> listar() {
        return boletas.findAllByOrderByIdDesc().stream().map(BoletaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<BoletaResponse> listarDe(String correo) {
        return boletas.findByUsuarioCorreoOrderByIdDesc(correo).stream().map(BoletaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public BoletaResponse buscar(Long id) {
        return boletas.findById(id)
                .map(BoletaResponse::de)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la boleta " + id));
    }
}
