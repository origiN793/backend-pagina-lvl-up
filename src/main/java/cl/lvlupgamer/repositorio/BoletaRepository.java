package cl.lvlupgamer.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.lvlupgamer.modelo.Boleta;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    
    List<Boleta> findAllByOrderByIdDesc();

    List<Boleta> findByUsuarioCorreoOrderByIdDesc(String correo);

    boolean existsByUsuarioId(Long usuarioId);
}
