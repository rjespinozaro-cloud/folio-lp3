// Archivo: src/main/java/folio_lp3/dto/ArsenalRequestDTO.java
package folio_lp3.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ArsenalRequestDTO {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String tecnologia;

    @Min(0) @Max(100)
    private Integer porcentaje;
}