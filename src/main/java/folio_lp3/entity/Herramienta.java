package folio_lp3.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Herramientas de ciberseguridad (Nmap, Metasploit, Burp Suite, etc.)
 */
@Entity
@Table(name = "herramienta", indexes = {
        @Index(name = "idx_nombre", columnList = "nombre")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Herramienta extends BaseEntity {

    @Column(name = "nombre", length = 100, nullable = false)
    @NotBlank(message = "El nombre de la herramienta no puede estar vacío")
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "creador", length = 100)
    private String creador;

    @Column(name = "nivel_dificultad", length = 50)
    private String nivelDificultad;

    @Column(name = "url_documentacion", length = 500)
    private String urlDocumentacion;
}