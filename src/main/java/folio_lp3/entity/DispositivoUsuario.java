package folio_lp3.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_dispositivos")
@Data
public class DispositivoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_email", nullable = false)
    private String usuarioEmail;

    @Column(name = "ip_origen", nullable = false, length = 45)
    private String ipOrigen;

    @Column(length = 255)
    private String dispositivo; // Ej: "Windows 11 (Chrome)"

    @Column(length = 255)
    private String lugar;       // Ej: "Trujillo, Peru"

    @Column(name = "tipo_dispositivo", length = 50)
    private String tipoDispositivo; // Ej: "DESKTOP", "MOBILE"

    @UpdateTimestamp
    @Column(name = "ultima_conexion")
    private LocalDateTime ultimaConexion;

    private boolean activa = true;
}