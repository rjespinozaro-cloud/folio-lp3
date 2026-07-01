package folio_lp3.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // <-- Asegúrate de importar esto
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDTO {
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @JsonProperty("password") // 👈 ESTO MAPEA "password" EN EL JSON A "contrasena" EN JAVA
    private String contrasena;
}