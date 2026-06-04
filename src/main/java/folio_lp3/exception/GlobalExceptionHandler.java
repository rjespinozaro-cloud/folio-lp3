package folio_lp3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador Global de Excepciones
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return crearRespuestaError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return crearRespuestaError(HttpStatus.BAD_REQUEST, "Argumento inválido: " + ex.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        return crearRespuestaError(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }
    
    private ResponseEntity<Map<String, Object>> crearRespuestaError(HttpStatus status, String mensaje) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", status.value());
        respuesta.put("error", status.getReasonPhrase());
        respuesta.put("message", mensaje);
        return new ResponseEntity<>(respuesta, status);
    }
}
