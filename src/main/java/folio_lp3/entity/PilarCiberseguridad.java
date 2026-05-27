package folio_lp3.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Pilar de Ciberseguridad - Representa un tema principal del curso
 * Vinculado a un Instructor y sus comandos/herramientas
 */
@Entity
@Table(name = "pilar_ciberseguridad", indexes = {
        @Index(name = "idx_nombre_instructor", columnList = "nombre_instructor"),
        @Index(name = "idx_entorno_id", columnList = "entorno_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PilarCiberseguridad extends BaseEntity {

    @Column(name = "nombre_pilar", length = 150, nullable = false)
    @NotBlank(message = "El nombre del pilar no puede estar vacío")
    private String nombrePilar;

    @Column(name = "nombre_instructor", length = 150, nullable = false)
    @NotBlank(message = "El nombre del instructor no puede estar vacío")
    private String nombreInstructor;

    @Column(name = "correo_contacto", length = 100, nullable = false)
    @Email(message = "El correo debe ser válido")
    private String correoContacto;

    @Column(name = "icono_url", length = 500)
    private String iconoUrl;

    @Column(name = "temario", columnDefinition = "LONGTEXT")
    private String temario;

    @Column(name = "enlaces_referencia", columnDefinition = "LONGTEXT")
    private String enlacesReferencia;

    @Column(name = "url_repositorio", length = 500)
    private String urlRepositorio;

    @Column(name = "horario_tutoria_inicio")
    private LocalTime horarioTutoriaInicio;

    @Column(name = "horario_tutoria_fin")
    private LocalTime horarioTutoriaFin;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entorno_id", foreignKey = @ForeignKey(name = "fk_pilar_entorno"))
    private Entorno entorno;

    @OneToMany(mappedBy = "pilar", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<DetalleComandoPilar> detalleComandos = new HashSet<>();

    @OneToMany(mappedBy = "pilarAsignado", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Usuario> instructores = new HashSet<>();

    @OneToMany(mappedBy = "pilar", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Consulta> consultas = new HashSet<>();
}