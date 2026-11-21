package cl.lvlupgamer.servicio;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import cl.lvlupgamer.error.ConflictoException;
import cl.lvlupgamer.error.RecursoNoEncontradoException;
import cl.lvlupgamer.modelo.Categoria;
import cl.lvlupgamer.repositorio.CategoriaRepository;
import cl.lvlupgamer.repositorio.ProductoRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categorias;
    private final ProductoRepository productos;

    public CategoriaService(CategoriaRepository categorias, ProductoRepository productos) {
        this.categorias = categorias;
        this.productos = productos;
    }

    public List<Categoria> listar() {
        return categorias.findAll(Sort.by("id"));
    }

    public Categoria buscar(Long id) {
        return categorias.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la categoría " + id));
    }

    public Categoria crear(Categoria datos) {
        String nombre = datos.getNombre().trim();
        if (categorias.existsByNombre(nombre)) {
            throw new ConflictoException("nombre", "Ya existe una categoría con ese nombre.");
        }
        datos.setId(null);
        datos.setNombre(nombre);
        return categorias.save(datos);
    }

    public Categoria actualizar(Long id, Categoria datos) {
        Categoria actual = buscar(id);
        String nombre = datos.getNombre().trim();
        if (!actual.getNombre().equalsIgnoreCase(nombre) && categorias.existsByNombre(nombre)) {
            throw new ConflictoException("nombre", "Ya existe una categoría con ese nombre.");
        }
        actual.setNombre(nombre);
        actual.setDescripcion(datos.getDescripcion());
        return categorias.save(actual);
    }

    public void eliminar(Long id) {
        Categoria categoria = buscar(id);
        if (productos.existsByCategoriaId(id)) {
            throw new ConflictoException(null, "No se puede eliminar una categoría que tiene productos.");
        }
        categorias.delete(categoria);
    }
}
