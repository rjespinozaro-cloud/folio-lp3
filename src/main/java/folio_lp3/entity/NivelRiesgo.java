package folio_lp3.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clasificación de nivel de riesgo de vulnerabilidades
 */
@Entity
@Table(name = "nivel_riesgo", indexes = {
        @Index(name = "idx_codigo", columnList = "codigo")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NivelRiesgo extends BaseEntity {

    @Column(name = "codigo", length = 50, nullable = false, unique = true)
    private String codigo;

    @Column(name = "descripcion", length = 255, nullable = false)
    private String descripcion;

    @Column(name = "nivel_numerico")
    private Integer nivelNumerico;
}