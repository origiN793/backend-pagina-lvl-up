package cl.lvlupgamer.servicio;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import cl.lvlupgamer.dto.ProductoRequest;
import cl.lvlupgamer.dto.ProductoResponse;
import cl.lvlupgamer.error.ConflictoException;
import cl.lvlupgamer.error.DatosInvalidosException;
import cl.lvlupgamer.error.RecursoNoEncontradoException;
import cl.lvlupgamer.modelo.Categoria;
import cl.lvlupgamer.modelo.Producto;
import cl.lvlupgamer.repositorio.CategoriaRepository;
import cl.lvlupgamer.repositorio.DetalleBoletaRepository;
import cl.lvlupgamer.repositorio.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productos;
    private final CategoriaRepository categorias;
    private final DetalleBoletaRepository detalles;

    
    
    public ProductoService(ProductoRepository productos, CategoriaRepository categorias,
                           DetalleBoletaRepository detalles) {
        this.productos = productos;
        this.categorias = categorias;
        this.detalles = detalles;
    }

    public List<ProductoResponse> listar(String categoria) {
        List<Producto> lista = (categoria == null || categoria.isBlank())
                ? productos.findAll(Sort.by("id"))
                : productos.findByCategoriaNombreOrderById(categoria);
        return lista.stream().map(ProductoResponse::de).toList();
    }

    
    
    public ProductoResponse buscar(Long id) {
        return ProductoResponse.de(entidad(id));
    }

    
    
    public ProductoResponse buscarPorCodigo(String codigo) {
        return productos.findByCodigo(codigo)
                .map(ProductoResponse::de)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto " + codigo));
    }

    public ProductoResponse crear(ProductoRequest datos) {
        String codigo = datos.codigo().trim();
        if (productos.existsByCodigo(codigo)) {
            throw new ConflictoException("codigo", "Ya existe un producto con ese código.");
        }
        Producto producto = new Producto();
        producto.setCodigo(codigo);
        copiarDatos(producto, datos);
        return ProductoResponse.de(productos.save(producto));
    }

    public ProductoResponse actualizar(Long id, ProductoRequest datos) {
        Producto producto = entidad(id);
        
        
        copiarDatos(producto, datos);
        return ProductoResponse.de(productos.save(producto));
    }

    public void eliminar(Long id) {
        Producto producto = entidad(id);
        
        if (detalles.existsByProductoId(id)) {
            throw new ConflictoException(null, "No se puede eliminar un producto que ya aparece en boletas.");
        }
        productos.delete(producto);
    }

    private Producto entidad(Long id) {
        return productos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto " + id));
    }

    private void copiarDatos(Producto producto, ProductoRequest datos) {
        Categoria categoria = categorias.findByNombre(datos.categoria())
                .orElseThrow(() -> new DatosInvalidosException("categoria",
                        "La categoría «" + datos.categoria() + "» no existe."));
        producto.setNombre(datos.nombre().trim());
        producto.setCategoria(categoria);
        producto.setPrecio(datos.precio());
        producto.setStock(datos.stock());
        producto.setStockCritico(datos.stockCritico());
        producto.setDescripcion(datos.descripcion());
        producto.setImagen(datos.imagen());
    }
}
