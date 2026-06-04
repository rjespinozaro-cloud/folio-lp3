package folio_lp3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO para PilarCiberseguridad
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PilarCiberseguridadDTO {
    private Long id;
    private String nombrePilar;
    private String nombreInstructor;
    private String correoContacto;
    private String iconoUrl;
    private String temario;
    private String enlacesReferencia;
    private String urlRepositorio;
    private LocalTime horarioTutoriaInicio;
    private LocalTime horarioTutoriaFin;
    private Long entornoId;
    private String entornoNombre;
    private Boolean activo;
    private Integer totalConsultas;
}
