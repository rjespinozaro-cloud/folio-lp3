// Archivo: src/main/java/folio_lp3/dto/PreguntaIADTO.java
package folio_lp3.dto;

import folio_lp3.enums.CalificacionIA;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO para PreguntaIA enriquecido para soportar RAG Contextual Vivo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreguntaIADTO {
    private Long id;
    private Long consultaId;
    private String preguntaEstudiante;
    private String respuestaIA;
    private Integer tokensConsumidos;
    private CalificacionIA calificacion;
    private LocalDateTime fechaHora;

    // === CAMPOS COMPLEMENTARIOS PARA CAPTURAR EL CONTEXTO VIVO DEL FRONTEND ===
    private String seccionActual;  // Ej: "laboratorios", "proyectos"
    private String contextoPagina;  // Contenido de texto extraído del DOM por el JS
}