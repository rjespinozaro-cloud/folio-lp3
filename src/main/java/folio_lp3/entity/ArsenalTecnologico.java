package folio_lp3.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Inventario de Arsenal Técnico y Porcentajes de Dominio de Herramientas
 */
@Entity
@Table(name = "arsenal_tecnologico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
public class ArsenalTecnologico extends BaseEntity {

    @Column(name = "tecnologia", length = 100, nullable = false, unique = true)
    @NotBlank(message = "El nombre de la tecnología/herramienta no puede estar vacío")
    private String tecnologia;

    @Column(name = "porcentaje", nullable = false)
    @Min(value = 0, message = "El dominio no puede ser menor a 0%")
    @Max(value = 100, message = "El dominio no puede superar el 100%")
    private Integer porcentaje;

    // 🛠️ ADICIÓN MULTIMEDIA: Ruta del Logo o Captura de pantalla de la herramienta en acción
    @Column(name = "ruta_captura", length = 255)
    private String rutaCaptura;
}