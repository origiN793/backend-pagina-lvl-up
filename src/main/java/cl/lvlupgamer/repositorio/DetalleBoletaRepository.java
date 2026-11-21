package cl.lvlupgamer.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.lvlupgamer.modelo.DetalleBoleta;

public interface DetalleBoletaRepository extends JpaRepository<DetalleBoleta, Long> {

    
    boolean existsByProductoId(Long productoId);
}
