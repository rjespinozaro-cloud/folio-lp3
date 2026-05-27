package folio_lp3.entity;


import folio_lp3.enums.CalificacionIA;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Historial del chat entre estudiante e IA
 * Almacena pregunta, respuesta, tokens consumidos y calificación
 */
@Entity
@Table(name = "pregunta_ia", indexes = {
        @Index(name = "idx_consulta_id", columnList = "consulta_id"),
        @Index(name = "idx_fecha_hora", columnList = "fecha_hora"),
        @Index(name = "idx_calificacion", columnList = "calificacion"),
        @Index(name = "idx_pregunta_consulta_fecha", columnList = "consulta_id,fecha_hora")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreguntaIA extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consulta_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_pregunta_consulta"))
    private Consulta consulta;

    @Column(name = "pregunta_estudiante", columnDefinition = "LONGTEXT", nullable = false)
    @NotBlank(message = "La pregunta no puede estar vacía")
    private String preguntaEstudiante;

    @Column(name = "respuesta_ia", columnDefinition = "LONGTEXT", nullable = false)
    @NotBlank(message = "La respuesta de IA no puede estar vacía")
    private String respuestaIA;

    @Column(name = "tokens_consumidos")
    @Builder.Default
    private Integer tokensConsumidos = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "calificacion", length = 50)
    private CalificacionIA calificacion;

    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private java.time.LocalDateTime fechaHora;

    @PrePersist
    protected void onCreate() {
        if (this.fechaHora == null) {
            this.fechaHora = java.time.LocalDateTime.now();
        }
    }
}