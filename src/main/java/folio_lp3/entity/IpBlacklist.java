package folio_lp3.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "ip_blacklist")
@Data
public class IpBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip_bloqueada", unique = true, nullable = false, length = 45)
    private String ipBloqueada;

    @Column(name = "motivo_bloqueo", length = 255)
    private String motivoBloqueo;

    @CreationTimestamp
    @Column(name = "fecha_bloqueo", updatable = false)
    private LocalDateTime fechaBloqueo;

    @Column(name = "expiracion_bloqueo")
    private LocalDateTime expiracionBloqueo;
}