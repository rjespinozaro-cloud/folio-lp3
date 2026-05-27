package folio_lp3.entity;

import folio_lp3.enums.RolUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Usuario del sistema (Administrador, Instructor, Estudiante)
 */
@Entity
@Table(name = "usuario", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_rol", columnList = "rol"),
        @Index(name = "idx_pilar_asignado_id", columnList = "pilar_asignado_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario extends BaseEntity {

    @Column(name = "email", length = 100, nullable = false, unique = true)
    @Email(message = "El email debe ser válido")
    private String email;

    @Column(name = "nombre_completo", length = 150)
    private String nombreCompleto;

    @Column(name = "contrasena", length = 255)
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", length = 50, nullable = false)
    @Builder.Default
    private RolUsuario rol = RolUsuario.ESTUDIANTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pilar_asignado_id",
                foreignKey = @ForeignKey(name = "fk_usuario_pilar"))
    private PilarCiberseguridad pilarAsignado;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Consulta> consultas = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Auditoria> auditorias = new HashSet<>();
}