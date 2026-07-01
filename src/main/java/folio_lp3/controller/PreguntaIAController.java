package folio_lp3.controller;

import folio_lp3.dto.PreguntaIADTO;
import folio_lp3.enums.CalificacionIA;
import folio_lp3.service.PreguntaIAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST seguro para interactuar con el Cyber Assistant mediante RAG.
 */
@RestController
@RequestMapping("/api/v1/preguntas-ia")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.security.cors.allowed-origins:http://localhost:3000}")
public class PreguntaIAController {
    
    private final PreguntaIAService preguntaService;
    
    @PostMapping("/preguntar")
    public ResponseEntity<PreguntaIADTO> procesarPreguntaCyberAssistant(@RequestBody PreguntaIADTO requestDTO) {
        PreguntaIADTO resultado = preguntaService.generarRespuestaContextual(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    @GetMapping("/lista/historial")
    public ResponseEntity<List<PreguntaIADTO>> listarHistorialCompleto() {
        return ResponseEntity.ok(preguntaService.listarHistorialCompleto());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PreguntaIADTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(preguntaService.obtenerPorId(id));
    }
    
    @GetMapping("/consulta/{consultaId}")
    public ResponseEntity<List<PreguntaIADTO>> listarPorConsulta(@PathVariable Long consultaId) {
        return ResponseEntity.ok(preguntaService.listarPorConsulta(consultaId));
    }
    
    @PutMapping("/{id}/calificar")
    public ResponseEntity<PreguntaIADTO> calificar(
            @PathVariable Long id,
            @RequestParam String calificacion) {
        CalificacionIA cal = CalificacionIA.valueOf(calificacion.toUpperCase());
        return ResponseEntity.ok(preguntaService.calificar(id, cal));
    }

    // 🚀 CORRECCIÓN: Endpoint de purga añadido para soportar la ruta '/api/v1/preguntas-ia/purgar'
    @DeleteMapping("/purgar")
    public ResponseEntity<?> purgarHistorialIA() {
        preguntaService.purgarTodoElHistorial(); // Asegúrate de que este método exista en tu PreguntaIAService
        return ResponseEntity.ok(Map.of(
            "exitoso", true,
            "mensaje", "Historial RAG de la IA purgado correctamente."
        ));
    }
}