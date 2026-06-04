package folio_lp3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear respuesta genérica de API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaGenericaDTO<T> {
    private boolean exitoso;
    private String mensaje;
    private T datos;
    private String timestamp;
    
    public static <T> RespuestaGenericaDTO<T> exitoso(String mensaje, T datos) {
        return RespuestaGenericaDTO.<T>builder()
                .exitoso(true)
                .mensaje(mensaje)
                .datos(datos)
                .timestamp(java.time.LocalDateTime.now().toString())
                .build();
    }
    
    public static <T> RespuestaGenericaDTO<T> error(String mensaje) {
        return RespuestaGenericaDTO.<T>builder()
                .exitoso(false)
                .mensaje(mensaje)
                .timestamp(java.time.LocalDateTime.now().toString())
                .build();
    }
}
