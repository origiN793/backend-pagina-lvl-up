package cl.lvlupgamer.error;

import java.util.Map;







public record ErrorApi(int status, String mensaje, Map<String, String> campos) {
}
