package cl.lvlupgamer.servicio;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cl.lvlupgamer.dto.RegistroRequest;
import cl.lvlupgamer.dto.UsuarioRequest;
import cl.lvlupgamer.dto.UsuarioResponse;
import cl.lvlupgamer.error.ConflictoException;
import cl.lvlupgamer.error.DatosInvalidosException;
import cl.lvlupgamer.error.RecursoNoEncontradoException;
import cl.lvlupgamer.modelo.Rol;
import cl.lvlupgamer.modelo.Usuario;
import cl.lvlupgamer.repositorio.BoletaRepository;
import cl.lvlupgamer.repositorio.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarios;
    private final BoletaRepository boletas;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarios, BoletaRepository boletas, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.boletas = boletas;
        this.encoder = encoder;
    }

    public List<UsuarioResponse> listar() {
        return usuarios.findAll(Sort.by("id")).stream().map(UsuarioResponse::de).toList();
    }

    public UsuarioResponse buscar(Long id) {
        return UsuarioResponse.de(entidad(id));
    }

    
    public UsuarioResponse registrar(RegistroRequest datos) {
        String run = normalizarRun(datos.run());
        String correo = normalizarCorreo(datos.correo());
        verificarRunLibre(run);
        verificarCorreoLibre(correo);

        Usuario usuario = new Usuario(run, datos.nombre().trim(), datos.apellidos().trim(), correo,
                encoder.encode(datos.password()), Rol.CLIENTE, datos.region(), datos.comuna(),
                datos.direccion().trim());
        return UsuarioResponse.de(usuarios.save(usuario));
    }

    
    public UsuarioResponse crear(UsuarioRequest datos) {
        if (datos.password() == null || datos.password().isBlank()) {
            throw new DatosInvalidosException("password", "La contraseña es obligatoria.");
        }
        String run = normalizarRun(datos.run());
        String correo = normalizarCorreo(datos.correo());
        verificarRunLibre(run);
        verificarCorreoLibre(correo);

        Usuario usuario = new Usuario(run, datos.nombre().trim(), datos.apellidos().trim(), correo,
                encoder.encode(datos.password()), datos.rol(), datos.region(), datos.comuna(),
                datos.direccion().trim());
        return UsuarioResponse.de(usuarios.save(usuario));
    }

    public UsuarioResponse actualizar(Long id, UsuarioRequest datos) {
        Usuario usuario = entidad(id);
        String correo = normalizarCorreo(datos.correo());
        if (!usuario.getCorreo().equals(correo)) {
            verificarCorreoLibre(correo);
        }
        
        usuario.setNombre(datos.nombre().trim());
        usuario.setApellidos(datos.apellidos().trim());
        usuario.setCorreo(correo);
        usuario.setRol(datos.rol());
        usuario.setRegion(datos.region());
        usuario.setComuna(datos.comuna());
        usuario.setDireccion(datos.direccion().trim());
        if (datos.password() != null && !datos.password().isBlank()) {
            usuario.setPassword(encoder.encode(datos.password()));
        }
        return UsuarioResponse.de(usuarios.save(usuario));
    }

    public void eliminar(Long id, String correoActual) {
        Usuario usuario = entidad(id);
        if (usuario.getCorreo().equals(correoActual)) {
            throw new ConflictoException(null, "No puedes eliminar tu propia cuenta.");
        }
        if (boletas.existsByUsuarioId(id)) {
            throw new ConflictoException(null, "No se puede eliminar un usuario que tiene boletas.");
        }
        usuarios.delete(usuario);
    }

    private Usuario entidad(Long id) {
        return usuarios.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + id));
    }

    private String normalizarRun(String run) {
        return run.trim().toUpperCase();
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase();
    }

    private void verificarRunLibre(String run) {
        if (usuarios.existsByRun(run)) {
            throw new ConflictoException("run", "Ya existe un usuario registrado con ese RUN.");
        }
    }

    private void verificarCorreoLibre(String correo) {
        if (usuarios.existsByCorreo(correo)) {
            throw new ConflictoException("correo", "Ya existe una cuenta con ese correo.");
        }
    }
}
