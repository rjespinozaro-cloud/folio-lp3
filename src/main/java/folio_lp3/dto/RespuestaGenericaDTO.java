package folio_lp3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO Estándar para las respuestas de la API.
 * Encapsula la data real (T) brindando metadata consistente al Frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaGenericaDTO<T> {
    private boolean exitoso;
    private String mensaje;
    private T datos;
    private LocalDateTime timestamp;
    
    public static <T> RespuestaGenericaDTO<T> exitoso(String mensaje, T datos) {
        return RespuestaGenericaDTO.<T>builder()
                .exitoso(true)
                .mensaje(mensaje)
                .datos(datos)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> RespuestaGenericaDTO<T> error(String mensaje, T datos) {
        return RespuestaGenericaDTO.<T>builder()
                .exitoso(false)
                .mensaje(mensaje)
                .datos(datos)
                .timestamp(LocalDateTime.now())
                .build();
    }
}