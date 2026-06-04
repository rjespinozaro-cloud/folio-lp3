package folio_lp3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para Entorno
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntornoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
}
