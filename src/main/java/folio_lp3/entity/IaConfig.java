package folio_lp3.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Parámetros Globales del Motor de Inteligencia Artificial (RAG Core Connection)
 */
@Entity
@Table(name = "ia_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IaConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proveedor_activo", length = 30, nullable = false)
    @NotBlank(message = "Debe definir un proveedor activo (gemini/ollama)")
    private String proveedorActivo;

    @Column(name = "nombre_modelo", length = 100, nullable = false)
    @NotBlank(message = "Debe asignar el identificador del modelo")
    private String nombreModelo;

    @Column(name = "gemini_api_key", length = 255)
    private String geminiApiKey;

    @Column(name = "ollama_url", length = 255)
    @Builder.Default
    private String ollamaUrl = "http://localhost:11434";

    @Column(name = "system_prompt", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "El prompt del sistema de la IA es mandatorio")
    private String systemPrompt;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @PrePersist
    @PreUpdate
    protected void onTimestamp() {
        this.actualizadoEn = LocalDateTime.now();
    }
}