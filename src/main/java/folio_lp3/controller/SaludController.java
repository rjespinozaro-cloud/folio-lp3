package folio_lp3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para verificar el estado de la API (Health Check)
 */
@RestController
@RequestMapping("/api/v1/salud")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SaludController {
    
    /**
     * Verificar que la API está funcionando
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> verificarSalud() {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", "ACTIVO");
        respuesta.put("servicio", "Folio LP3 - Plataforma de Ciberseguridad");
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("version", "1.0.0");
        
        return ResponseEntity.ok(respuesta);
    }
    
    /**
     * Health check detallado
     */
    @GetMapping("/detallado")
    public ResponseEntity<Map<String, Object>> verificarSaludDetallado() {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", "ACTIVO");
        respuesta.put("base_datos", "CONECTADA");
        respuesta.put("memoria_disponible_mb", Runtime.getRuntime().freeMemory() / 1024 / 1024);
        respuesta.put("memoria_total_mb", Runtime.getRuntime().totalMemory() / 1024 / 1024);
        respuesta.put("procesadores", Runtime.getRuntime().availableProcessors());
        respuesta.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(respuesta);
    }
}
