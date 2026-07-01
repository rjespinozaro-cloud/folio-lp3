package folio_lp3.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Herramientas de ciberseguridad (Nmap, Metasploit, Burp Suite, etc.)
 * Estructura optimizada para desacoplar de llamadas recursivas en memoria con soporte cascada.
 */
@Entity
@Table(name = "herramienta", indexes = {
        @Index(name = "idx_nombre", columnList = "nombre")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Herramienta extends BaseEntity {

    @Column(name = "nombre", length = 100, nullable = false)
    @NotBlank(message = "El nombre de la herramienta no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    @Size(max = 5000, message = "La descripción no puede superar los 5000 caracteres")
    private String descripcion;

    @Column(name = "creador", length = 100)
    @Size(max = 100, message = "El nombre del creador no puede superar los 100 caracteres")
    private String creador;

    @Column(name = "nivel_dificultad", length = 50)
    @Size(max = 50, message = "El nivel de dificultad no puede superar los 50 caracteres")
    private String nivelDificultad;

    @Column(name = "url_documentacion", length = 500)
    @Size(max = 500, message = "La URL de documentación no puede superar los 500 caracteres")
    private String urlDocumentacion;

    // RELACIÓN AGREGADA: Mapeo de comandos en cascada
    @OneToMany(mappedBy = "herramienta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<DetalleComandoPilar> comandos = new ArrayList<>();
}