package folio_lp3.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Pilar de Ciberseguridad - Representa una especialidad o módulo principal del Portafolio.
 * Vinculado al operador (Ronaldino) y sus laboratorios/comandos asociados para los Reclutadores.
 */
@Entity
@Table(name = "pilar_ciberseguridad", indexes = {
        @Index(name = "idx_nombre_instructor", columnList = "nombre_instructor"),
        @Index(name = "idx_entorno_id", columnList = "entorno_id")
})
@Getter // CORRECCIÓN: Reemplazamos @Data por Getter/Setter explícitos
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"detalleComandos", "instructores", "consultas"}) // CORRECCIÓN: Evita el bucle infinito y carga EAGER oculta
@EqualsAndHashCode(callSuper = true, exclude = {"detalleComandos", "instructores", "consultas"}) // CORRECCIÓN: Clave para JPA
public class PilarCiberseguridad extends BaseEntity {

    @Column(name = "nombre_pilar", length = 150, nullable = false)
    @NotBlank(message = "El nombre del pilar no puede estar vacío")
    @Size(max = 150, message = "El nombre del pilar no puede superar los 150 caracteres")
    private String nombrePilar;

    @Column(name = "nombre_instructor", length = 150, nullable = false)
    @NotBlank(message = "El nombre del autor/instructor no puede estar vacío")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombreInstructor;

    @Column(name = "correo_contacto", length = 100, nullable = false)
    @Email(message = "El correo debe ser válido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    private String correoContacto;

    @Column(name = "icono_url", length = 500)
    @Size(max = 500, message = "La URL del icono no puede superar los 500 caracteres")
    private String iconoUrl;

    // CORRECCIÓN: Protección ante DoS controlando el tamaño máximo permitido en la capa de persistencia
    @Column(name = "temario", columnDefinition = "LONGTEXT")
    @Size(max = 50000, message = "El contenido del temario excede el límite seguro permitido")
    private String temario;

    @Column(name = "enlaces_referencia", columnDefinition = "LONGTEXT")
    @Size(max = 20000, message = "Los enlaces de referencia exceden el límite seguro permitido")
    private String enlacesReferencia;

    @Column(name = "url_repositorio", length = 500)
    @Size(max = 500, message = "La URL del repositorio no puede superar los 500 caracteres")
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