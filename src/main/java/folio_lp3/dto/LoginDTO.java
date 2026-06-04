package folio_lp3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para Autenticación - Solicitud de Login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDTO {
    private String email;
    private String contrasena;
}
