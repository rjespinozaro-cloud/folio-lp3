package folio_lp3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para Respuesta de Autenticación - Token JWT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDTO {
    private String token;
    private String tipo;
    private Long usuarioId;
    private String email;
    private String nombreCompleto;
    private String rol;
}
