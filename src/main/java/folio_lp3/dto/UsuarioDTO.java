package folio_lp3.dto;

import folio_lp3.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para Usuario - Transferencia de datos sin exponer la entidad completa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;
    private String email;
    private String nombreCompleto;
    private RolUsuario rol;
    private Long pilarAsignadoId;
    private Boolean activo;
    private LocalDateTime ultimoAcceso;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
