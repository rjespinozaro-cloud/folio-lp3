package folio_lp3.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Subtemas o áreas específicas de ciberseguridad (OSINT, Privilege Escalation, etc.)
 */
@Entity
@Table(name = "subtema", indexes = {
        @Index(name = "idx_codigo", columnList = "codigo")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subtema extends BaseEntity {

    @Column(name = "codigo", length = 50, nullable = false, unique = true)
    private String codigo;

    @Column(name = "descripcion", length = 255, nullable = false)
    private String descripcion;
}