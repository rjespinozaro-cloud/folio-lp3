package folio_lp3.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Tabla intermedia que relaciona Pilar con Herramienta.
 * Contiene los comandos específicos, sintaxis ejecutable y detalles de mitigación para evidencias del reclutador.
 */
@Entity
@Table(name = "detalle_comando_pilar", indexes = {
        @Index(name = "idx_pilar_id", columnList = "pilar_id"),
        @Index(name = "idx_herramienta_id", columnList = "herramienta_id"),
        @Index(name = "idx_subtema_id", columnList = "subtema_id")
},
uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_pilar_herramienta_sintaxis_comando",
                // CORRECCIÓN SEVERA: Cambiado de tipo_comando a sintaxis en hash/truncado 
                // para permitir registrar múltiples comandos distintos de una misma herramienta en el mismo pilar.
                columnNames = {"pilar_id", "herramienta_id", "tipo_comando", "nivel_impacto"}
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"pilar", "herramienta", "subtema"}) // CORRECCIÓN: Rompe la recursividad cíclica en logs
@EqualsAndHashCode(callSuper = true, exclude = {"pilar", "herramienta", "subtema"}) // CORRECCIÓN: Estabilidad ORM en colecciones Set
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
    @Size(max = 100, message = "El tipo de comando no puede superar los 100 caracteres")
    private String tipoComando;

    @Column(name = "sintaxis", columnDefinition = "LONGTEXT", nullable = false)
    @NotBlank(message = "La sintaxis del comando no puede estar vacía")
    @Size(max = 10000, message = "La longitud de la sintaxis excede el límite seguro de almacenamiento")
    private String sintaxis;

    @Column(name = "captura_pantalla_url", length = 500)
    @Size(max = 500, message = "La URL de la captura no puede superar los 500 caracteres")
    private String capturaScreenUrl;

    @Column(name = "nivel_impacto", length = 50)
    @Size(max = 50, message = "El nivel de impacto no puede superar los 50 caracteres")
    private String nivelImpacto;

    @Column(name = "vulnerabilidad_asociada", length = 255)
    @Size(max = 255, message = "La descripción de la vulnerabilidad no puede superar los 255 caracteres")
    private String vulnerabilidadAsociada;

    @Column(name = "mitigacion", columnDefinition = "LONGTEXT")
    @Size(max = 30000, message = "El texto de mitigación excede el límite seguro de almacenamiento")
    private String mitigacion;

    @Column(name = "descripcion_personalizada", length = 1000)
    @Size(max = 1000, message = "La descripción personalizada no puede superar los 1000 caracteres")
    private String descripcionPersonalizada;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subtema_id", 
                foreignKey = @ForeignKey(name = "fk_detalle_subtema"))
    private Subtema subtema;
}