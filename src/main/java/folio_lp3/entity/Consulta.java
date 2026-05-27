package folio_lp3.entity;


import folio_lp3.enums.EstadoConsulta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Consulta - Solicitud de un estudiante a través de un pilar específico
 * Contiene el historial de preguntas-respuestas (PreguntaIA)
 */
@Entity
@Table(name = "consulta", indexes = {
        @Index(name = "idx_estudiante_id", columnList = "estudiante_id"),
        @Index(name = "idx_pilar_id", columnList = "pilar_id"),
        @Index(name = "idx_estado", columnList = "estado"),
        @Index(name = "idx_fecha_creacion", columnList = "fecha_creacion"),
        @Index(name = "idx_ultima_actividad", columnList = "ultima_actividad"),
        @Index(name = "idx_consulta_estado_fecha", columnList = "estado,fecha_creacion")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consulta extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_consulta_estudiante"))
    @NotNull(message = "El estudiante no puede ser nulo")
    private Usuario estudiante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pilar_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_consulta_pilar"))
    @NotNull(message = "El pilar no puede ser nulo")
    private PilarCiberseguridad pilar;

    @Column(name = "tema_principal", length = 255, nullable = false)
    @NotBlank(message = "El tema principal no puede estar vacío")
    private String temaPrincipal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoConsulta estado = EstadoConsulta.PENDIENTE;

    @Column(name = "cantidad_tokens_usados")
    @Builder.Default
    private Integer cantidadTokensUsados = 0;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "ultima_actividad")
    private LocalDateTime ultimaActividad;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "motivo_cancelacion", length = 255)
    private String motivoCancelacion;

    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PreguntaIA> preguntasIA = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.ultimaActividad == null) {
            this.ultimaActividad = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.ultimaActividad = LocalDateTime.now();
    }
}