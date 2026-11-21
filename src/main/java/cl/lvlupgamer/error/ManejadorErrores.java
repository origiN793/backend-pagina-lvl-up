package cl.lvlupgamer.error;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;




@RestControllerAdvice
public class ManejadorErrores {

    
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorApi> noEncontrado(RecursoNoEncontradoException ex) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of());
    }

    
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorApi> sinStock(StockInsuficienteException ex) {
        return responder(HttpStatus.CONFLICT, ex.getMessage(), Map.of());
    }

    
    
    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorApi> conflicto(ConflictoException ex) {
        return responder(HttpStatus.CONFLICT, ex.getMessage(), campo(ex.getCampo(), ex.getMessage()));
    }

    
    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<ErrorApi> datosDeNegocioInvalidos(DatosInvalidosException ex) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage(), campo(ex.getCampo(), ex.getMessage()));
    }

    
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorApi> credenciales(CredencialesInvalidasException ex) {
        return responder(HttpStatus.UNAUTHORIZED, ex.getMessage(), Map.of());
    }

    
    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<ErrorApi> tokenInvalido(TokenInvalidoException ex) {
        return responder(HttpStatus.UNAUTHORIZED, ex.getMessage(), Map.of());
    }

    
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApi> datosInvalidos(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return responder(HttpStatus.BAD_REQUEST, "Revisa los datos marcados", campos);
    }

    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorApi> cuerpoIlegible(HttpMessageNotReadableException ex) {
        return responder(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no es válido.", Map.of());
    }

    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorApi> tipoIncorrecto(MethodArgumentTypeMismatchException ex) {
        return responder(HttpStatus.BAD_REQUEST, "El valor «" + ex.getValue() + "» no es válido", Map.of());
    }

    private Map<String, String> campo(String nombre, String mensaje) {
        return nombre == null ? Map.of() : Map.of(nombre, mensaje);
    }

    private ResponseEntity<ErrorApi> responder(HttpStatus estado, String mensaje, Map<String, String> campos) {
        return ResponseEntity.status(estado).body(new ErrorApi(estado.value(), mensaje, campos));
    }
}
