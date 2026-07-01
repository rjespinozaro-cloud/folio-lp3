package folio_lp3.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entidad para la Gestión de Prácticas, Laboratorios y Reportes de Ciberseguridad.
 */
@Entity
@Table(name = "practicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
public class Practicas extends BaseEntity {

    @Column(name = "titulo", length = 150, nullable = false)
    @NotBlank(message = "El título de la práctica no puede estar vacío")
    @Size(max = 150, message = "El título excede los 150 caracteres")
    private String titulo;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "La descripción del laboratorio es obligatoria")
    private String descripcion;

    @Column(name = "categoria", length = 100)
    @Size(max = 100, message = "La categoría excede los 100 caracteres")
    private String categoria;

    @Column(name = "ruta_documento", length = 255)
    private String rutaDocumento;

    @Column(name = "documento_tipo", length = 50)
    private String documentoTipo;

    // 🛡️ ADICIÓN TÁCTICA: Hash de verificación para validar la integridad en el SiemLog/Evidencias
    @Column(name = "hash_verificacion", length = 64)
    private String hashVerificacion;
}