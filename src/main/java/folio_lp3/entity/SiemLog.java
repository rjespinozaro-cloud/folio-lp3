package folio_lp3.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "siem_log")
public class SiemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(length = 255)
    private String endpoint;

    @Column(name = "metodo_http", length = 10)
    private String metodoHttp;

    @Column(name = "status_http")
    private Integer statusHttp;

    @Column(name = "usuario_email", length = 100)
    private String usuarioEmail;

    // CORRECCIÓN SEVERA: Atributo faltante inyectado para almacenar la traza de auditoría de excepciones
    @Column(name = "detalles_error", columnDefinition = "TEXT")
    private String detallesError;
}