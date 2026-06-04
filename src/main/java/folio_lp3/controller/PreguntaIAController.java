package folio_lp3.controller;

import folio_lp3.dto.PreguntaIADTO;
import folio_lp3.enums.CalificacionIA;
import folio_lp3.service.PreguntaIAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para PreguntaIA (Chat con IA)
 */
@RestController
@RequestMapping("/preguntas-ia")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PreguntaIAController {
    
    private final PreguntaIAService preguntaService;
    
    @PostMapping
    public ResponseEntity<PreguntaIADTO> crearPregunta(
            @RequestParam Long consultaId,
            @RequestParam String preguntaEstudiante,
            @RequestParam String respuestaIA,
            @RequestParam(required = false) Integer tokensConsumidos) {
        PreguntaIADTO resultado = preguntaService.crearPregunta(consultaId, preguntaEstudiante, respuestaIA, tokensConsumidos);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PreguntaIADTO> obtenerPorId(@PathVariable Long id) {
        PreguntaIADTO pregunta = preguntaService.obtenerPorId(id);
        return ResponseEntity.ok(pregunta);
    }
    
    @GetMapping
    public ResponseEntity<List<PreguntaIADTO>> listarTodas() {
        List<PreguntaIADTO> preguntas = preguntaService.listarTodas();
        return ResponseEntity.ok(preguntas);
    }
    
    @GetMapping("/consulta/{consultaId}")
    public ResponseEntity<List<PreguntaIADTO>> listarPorConsulta(@PathVariable Long consultaId) {
        List<PreguntaIADTO> preguntas = preguntaService.listarPorConsulta(consultaId);
        return ResponseEntity.ok(preguntas);
    }
    
    @PutMapping("/{id}/calificar")
    public ResponseEntity<PreguntaIADTO> calificar(
            @PathVariable Long id,
            @RequestParam String calificacion) {
        CalificacionIA cal = CalificacionIA.valueOf(calificacion.toUpperCase());
        PreguntaIADTO resultado = preguntaService.calificar(id, cal);
        return ResponseEntity.ok(resultado);
    }
}

