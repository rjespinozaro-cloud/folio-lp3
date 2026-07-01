package folio_lp3.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IaConfigRequestDTO {
    private Long id; // 🔥 Esencial para saber qué fila se está editando en la tabla
    private String proveedorActivo;
    private String nombreModelo;
    private String geminiApiKey;
    private String ollamaUrl;
    private String systemPrompt;
}