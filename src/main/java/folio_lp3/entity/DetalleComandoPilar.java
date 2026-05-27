package folio_lp3.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tabla intermedia que relaciona Pilar con Herramienta
 * Contiene los comandos específicos, sintaxis, y detalles de mitigación
 */
@Entity
@Table(name = "detalle_comando_pilar", indexes = {
        @Index(name = "idx_pilar_id", columnList = "pilar_id"),
        @Index(name = "idx_herramienta_id", columnList = "herramienta_id"),
        @Index(name = "idx_subtema_id", columnList = "subtema_id")
},
uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_pilar_herramienta_comando",
                columnNames = {"pilar_id", "herramienta_id", "tipo_comando"}
        )
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleComandoPilar extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pilar_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_detalle_pilar"))
    private PilarCiberseguridad pilar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "herramienta_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_detalle_herramienta"))
    private Herramienta herramienta;

    @Column(name = "tipo_comando", length = 100)
    private String tipoComando;

    @Column(name = "sintaxis", columnDefinition = "LONGTEXT", nullable = false)
    @NotBlank(message = "La sintaxis del comando no puede estar vacía")
    private String sintaxis;

    @Column(name = "captura_pantalla_url", length = 500)
    private String capturaScreenUrl;

    @Column(name = "nivel_impacto", length = 50)
    private String nivelImpacto;

    @Column(name = "vulnerabilidad_asociada", length = 255)
    private String vulnerabilidadAsociada;

    @Column(name = "mitigacion", columnDefinition = "LONGTEXT")
    private String mitigacion;

    @Column(name = "descripcion_personalizada")
    private String descripcionPersonalizada;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subtema_id", 
                foreignKey = @ForeignKey(name = "fk_detalle_subtema"))
    private Subtema subtema;
}