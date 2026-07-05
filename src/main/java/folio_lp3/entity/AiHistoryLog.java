package folio_lp3.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_history_log")
public class AiHistoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_email", nullable = false)
    private String usuarioEmail;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String prompt;

    @Column(columnDefinition = "TEXT")
    private String respuesta;

    @Column(name = "modelo_utilizado")
    private String modeloUtilizado; // Ejemplo: 'gpt-4o', 'claude-3-sonnet'

    @Column(name = "tokens_prompt")
    private Integer tokensPrompt;

    @Column(name = "tokens_completion")
    private Integer tokensCompletion;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // Constructores, Getters y Setters
}