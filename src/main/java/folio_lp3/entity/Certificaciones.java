package folio_lp3.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Gestión de Certificaciones y Credenciales de la industria (CompTIA, Cisco, Google)
 */
@Entity
@Table(name = "certificaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
public class Certificaciones extends BaseEntity {

    @Column(name = "nombre", length = 150, nullable = false)
    @NotBlank(message = "El nombre de la credencial no puede estar vacío")
    @Size(max = 150, message = "El nombre de la credencial excede los 150 caracteres")
    private String nombre;

    @Column(name = "institucion", length = 150, nullable = false)
    @NotBlank(message = "La institución emisora no puede estar vacía")
    @Size(max = 150, message = "La institución excede los 150 caracteres")
    private String institucion;

    @Column(name = "codigo_id", length = 100)
    @Size(max = 100, message = "El código ID excede los 100 caracteres")
    private String codigoId;

    @Column(name = "url_validacion", columnDefinition = "TEXT")
    private String urlValidacion;

    // 🎓 ADICIÓN MULTIMEDIA: Almacenar la insignia o diploma digitalizado
    @Column(name = "ruta_imagen", length = 255)
    private String rutaImagen;

    @Column(name = "imagen_tipo", length = 50)
    private String imagenTipo;
}