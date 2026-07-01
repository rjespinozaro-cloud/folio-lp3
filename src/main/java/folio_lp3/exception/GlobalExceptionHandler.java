package folio_lp3.exception;

import folio_lp3.dto.RespuestaGenericaDTO;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Manejador Global de Excepciones.
 * Centraliza el manejo de errores y devuelve respuestas consistentes usando {@link RespuestaGenericaDTO}.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura archivos estáticos no encontrados (como favicon.ico)
     * y devuelve un 404 en lugar de un 500 (Solución al error del log).
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<RespuestaGenericaDTO<Object>> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(RespuestaGenericaDTO.error("Recurso no encontrado: " + ex.getResourcePath(), null));
    }

    /**
     * Captura errores de validación de DTOs anotados con @Valid.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RespuestaGenericaDTO<Map<String, List<String>>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        return ResponseEntity.badRequest()
                .body(RespuestaGenericaDTO.error("Error de validación", errors));
    }

    /**
     * Captura errores de integridad de la base de datos, como violaciones de claves únicas.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<RespuestaGenericaDTO<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String mensaje = "Error de integridad de datos. Es posible que un registro con un valor único (ej. email) ya exista.";
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(RespuestaGenericaDTO.error(mensaje, null));
    }

    /**
     * Captura excepciones de acceso denegado personalizadas (ej. aislamiento de datos).
     */
    @ExceptionHandler(AccesoDenegadoException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<RespuestaGenericaDTO<Object>> handleAccesoDenegado(AccesoDenegadoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(RespuestaGenericaDTO.error(ex.getMessage(), null));
    }

    /**
     * Captura excepciones generales de la aplicación para errores no esperados.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RespuestaGenericaDTO<Object>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(RespuestaGenericaDTO.error(ex.getMessage(), null));
    }

    /**
     * Captura argumentos ilegales, por ejemplo, al convertir un String a un Enum.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RespuestaGenericaDTO<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(RespuestaGenericaDTO.error("Argumento inválido: " + ex.getMessage(), null));
    }

    /**
     * Manejador genérico para cualquier otra excepción no capturada.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<RespuestaGenericaDTO<Object>> handleGeneralException(Exception ex) {
        // Imprime el error en la consola del servidor para que sepas qué falló realmente
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RespuestaGenericaDTO.error("Ocurrió un error inesperado en el servidor.", null));
    }
}