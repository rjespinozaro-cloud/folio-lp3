package folio_lp3.dto;

import folio_lp3.enums.CalificacionIA;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para PreguntaIA
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreguntaIADTO {
    private Long id;
    private Long consultaId;
    private String preguntaEstudiante;
    private String respuestaIA;
    private Integer tokensConsumidos;
    private CalificacionIA calificacion;
    private LocalDateTime fechaHora;
}
