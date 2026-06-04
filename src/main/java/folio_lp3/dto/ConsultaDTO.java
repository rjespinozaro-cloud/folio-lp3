package folio_lp3.dto;

import folio_lp3.enums.EstadoConsulta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para Consulta
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaDTO {
    private Long id;
    private Long estudianteId;
    private String estudianteNombre;
    private Long pilarId;
    private String pilarNombre;
    private String temaPrincipal;
    private EstadoConsulta estado;
    private Integer cantidadTokensUsados;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimaActividad;
    private LocalDateTime fechaCierre;
    private String motivoCancelacion;
    private Integer totalPreguntasIA;
}
