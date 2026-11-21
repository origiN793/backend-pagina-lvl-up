package cl.lvlupgamer.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import cl.lvlupgamer.modelo.Categoria;
import cl.lvlupgamer.modelo.Producto;
import cl.lvlupgamer.modelo.Rol;
import cl.lvlupgamer.modelo.Usuario;
import cl.lvlupgamer.repositorio.CategoriaRepository;
import cl.lvlupgamer.repositorio.ProductoRepository;
import cl.lvlupgamer.repositorio.UsuarioRepository;










@Component
public class DatosIniciales implements CommandLineRunner {

    private final CategoriaRepository categorias;
    private final ProductoRepository productos;
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    public DatosIniciales(CategoriaRepository categorias, ProductoRepository productos,
                          UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.categorias = categorias;
        this.productos = productos;
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (categorias.count() == 0) {
            cargarCatalogo();
        }
        if (usuarios.count() == 0) {
            cargarUsuarios();
        }
    }

    private void cargarCatalogo() {
        Map<String, Categoria> cat = new HashMap<>();
        for (String nombre : List.of("Juegos de Mesa", "Accesorios", "Consolas", "Computadores",
                "Sillas Gamers", "Monitores", "Poleras Personalizadas")) {
            cat.put(nombre, categorias.save(new Categoria(nombre, null)));
        }

        productos.saveAll(List.of(
                new Producto("JM001", "Catan", cat.get("Juegos de Mesa"), 29990, 12, 3,
                        "Juego de mesa de estrategia y comercio para 3 a 4 jugadores.",
                        "/images/catan.jpg"),
                new Producto("JM002", "Carcassonne", cat.get("Juegos de Mesa"), 24990, 8, 2,
                        "Construye ciudades y caminos en este clásico juego de fichas.",
                        "/images/Carcassonne.jpg"),
                new Producto("AC001", "Control Inalámbrico Xbox", cat.get("Accesorios"), 59990, 15, 4,
                        "Control oficial con conectividad Bluetooth.",
                        "/images/control_Xbox.jpg"),
                new Producto("AC002", "Audífonos Gamer HyperX", cat.get("Accesorios"), 79990, 6, 2,
                        "Sonido envolvente y micrófono desmontable.",
                        "/images/Audifonos_Gamer_HyperX.jpg"),
                new Producto("CO001", "PlayStation 5", cat.get("Consolas"), 549990, 4, 2,
                        "Consola de nueva generación con SSD ultra rápido.",
                        "/images/PlayStation_5.jpg"),
                new Producto("CO002", "Nintendo Switch OLED", cat.get("Consolas"), 329990, 7, 2,
                        "Pantalla OLED de 7 pulgadas, modo portátil y modo TV.",
                        "/images/Nintendo_Switch_OLED.jpg"),
                new Producto("CG001", "Notebook Gamer ASUS TUF", cat.get("Computadores"), 899990, 3, 1,
                        "Procesador Ryzen 7 y tarjeta gráfica RTX.",
                        "/images/Notebook_Gamer_ASUS_TUF.jpg"),
                new Producto("SG001", "Silla Gamer", cat.get("Sillas Gamers"), 349990, 5, 2,
                        "Ergonomía premium y soporte lumbar ajustable.",
                        "/images/Silla_Gamer.jpg"),
                new Producto("MS001", "Mouse Gamer Logitech G502", cat.get("Accesorios"), 39990, 20, 5,
                        "Sensor óptico de alta precisión y 11 botones.",
                        "/images/Mouse_Gamer_Logitech_G502.jpg"),
                new Producto("MO001", "Monitor Gamer LG 27 pulgadas", cat.get("Monitores"), 289990, 6, 2,
                        "Panel IPS 165Hz y 1ms de respuesta.",
                        "/images/Monitor_Gamer_LG_27_pulgadas.jpg"),
                new Producto("PP001", "Alfombrilla XL RGB", cat.get("Accesorios"), 19990, 0, 3,
                        "Mousepad extendido con iluminación RGB.",
                        "/images/Alfombrilla_XL_RGB.jpg"),
                new Producto("PC001", "Polera Gamer Level-Up", cat.get("Poleras Personalizadas"), 14990, 25, 5,
                        "Polera oficial de la comunidad Level-Up.",
                        "/images/Polera_Gamer_Level-Up.jpg")));
    }

    private void cargarUsuarios() {
        usuarios.saveAll(List.of(
                usuario("191102226", "Javiera", "Muñoz Rojas", "javiera.munoz@duoc.cl", "Admin123",
                        Rol.ADMINISTRADOR, "Región Metropolitana de Santiago", "Santiago", "Av. Siempre Viva 123"),
                usuario("128574936", "Matías", "Contreras Silva", "matias.contreras@gmail.com", "Vend1234",
                        Rol.VENDEDOR, "Región del Biobío", "Concepción", "Calle Los Aromos 456"),
                usuario("205931189", "Fernanda", "López Díaz", "fernanda.lopez@gmail.com", "Cli12345",
                        Rol.CLIENTE, "Región de Valparaíso", "Viña del Mar", "Pasaje Las Rosas 789")));
    }

    private Usuario usuario(String run, String nombre, String apellidos, String correo, String clave,
                            Rol rol, String region, String comuna, String direccion) {
        return new Usuario(run, nombre, apellidos, correo, encoder.encode(clave), rol, region, comuna, direccion);
    }
}
