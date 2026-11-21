package cl.lvlupgamer.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.lvlupgamer.modelo.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    
    
    List<Producto> findByCategoriaNombreOrderById(String nombreCategoria);

    
    Optional<Producto> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCategoriaId(Long categoriaId);
}
