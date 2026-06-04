package folio_lp3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para Herramienta
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HerramientaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String creador;
    private String nivelDificultad;
    private String urlDocumentacion;
}
