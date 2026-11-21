package cl.lvlupgamer.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import cl.lvlupgamer.dto.RegistroRequest;
import cl.lvlupgamer.dto.UsuarioResponse;
import cl.lvlupgamer.error.ConflictoException;
import cl.lvlupgamer.modelo.Rol;
import cl.lvlupgamer.modelo.Usuario;
import cl.lvlupgamer.repositorio.BoletaRepository;
import cl.lvlupgamer.repositorio.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarios;
    @Mock
    private BoletaRepository boletas;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private UsuarioService servicio;

    @BeforeEach
    void preparar() {
        servicio = new UsuarioService(usuarios, boletas, encoder);
    }

    private RegistroRequest registro() {
        return new RegistroRequest("19011022k", "Ana", "Pérez", "  ANA@Duoc.cl ", "clave123",
                "Región de Valparaíso", "Viña del Mar", "Calle 1");
    }

    @Test
    void elRegistroPublicoSiempreCreaUnClienteConLaClaveEncriptada() {
        when(usuarios.existsByRun("19011022K")).thenReturn(false);
        when(usuarios.existsByCorreo("ana@duoc.cl")).thenReturn(false);
        when(usuarios.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponse creado = servicio.registrar(registro());

        assertEquals(Rol.CLIENTE, creado.rol());
        assertEquals("ana@duoc.cl", creado.correo());
        assertEquals("19011022K", creado.run());

        ArgumentCaptor<Usuario> guardado = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarios).save(guardado.capture());
        assertNotEquals("clave123", guardado.getValue().getPassword());
        assertTrue(encoder.matches("clave123", guardado.getValue().getPassword()));
    }

    @Test
    void unCorreoRepetidoLanzaConflicto() {
        when(usuarios.existsByRun("19011022K")).thenReturn(false);
        when(usuarios.existsByCorreo("ana@duoc.cl")).thenReturn(true);

        ConflictoException error = assertThrows(ConflictoException.class, () -> servicio.registrar(registro()));

        assertEquals("correo", error.getCampo());
        verify(usuarios, never()).save(any(Usuario.class));
    }

    @Test
    void unRunRepetidoLanzaConflicto() {
        when(usuarios.existsByRun("19011022K")).thenReturn(true);

        ConflictoException error = assertThrows(ConflictoException.class, () -> servicio.registrar(registro()));

        assertEquals("run", error.getCampo());
    }
}
