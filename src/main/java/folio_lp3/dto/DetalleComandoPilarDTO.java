package folio_lp3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para DetalleComandoPilar
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleComandoPilarDTO {
    private Long id;
    private Long pilarId;
    private String pilarNombre;
    private Long herramientaId;
    private String herramientaNombre;
    private String tipoComando;
    private String sintaxis;
    private String capturaScreenUrl;
    private String nivelImpacto;
    private String vulnerabilidadAsociada;
    private String mitigacion;
    private String descripcionPersonalizada;
    private Long subtemaId;
    private String subtemaCodigo;
    private Boolean activo;
}
