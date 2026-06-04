package folio_lp3.controller;

import folio_lp3.dto.PilarCiberseguridadDTO;
import folio_lp3.service.PilarCiberseguridadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para PilarCiberseguridad
 */
@RestController
@RequestMapping("/pilares")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PilarCiberseguridadController {
    
    private final PilarCiberseguridadService pilarService;
    
    @PostMapping
    public ResponseEntity<PilarCiberseguridadDTO> crearPilar(@RequestBody PilarCiberseguridadDTO pilarDTO) {
        PilarCiberseguridadDTO resultado = pilarService.crearPilar(pilarDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PilarCiberseguridadDTO> obtenerPorId(@PathVariable Long id) {
        PilarCiberseguridadDTO pilar = pilarService.obtenerPorId(id);
        return ResponseEntity.ok(pilar);
    }
    
    @GetMapping
    public ResponseEntity<List<PilarCiberseguridadDTO>> listarTodos() {
        List<PilarCiberseguridadDTO> pilares = pilarService.listarTodos();
        return ResponseEntity.ok(pilares);
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<PilarCiberseguridadDTO>> listarActivos() {
        List<PilarCiberseguridadDTO> pilares = pilarService.listarActivos();
        return ResponseEntity.ok(pilares);
    }
    
    @GetMapping("/entorno/{entornoId}")
    public ResponseEntity<List<PilarCiberseguridadDTO>> listarPorEntorno(@PathVariable Long entornoId) {
        List<PilarCiberseguridadDTO> pilares = pilarService.listarPorEntorno(entornoId);
        return ResponseEntity.ok(pilares);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PilarCiberseguridadDTO> actualizar(
            @PathVariable Long id,
            @RequestBody PilarCiberseguridadDTO pilarDTO) {
        PilarCiberseguridadDTO resultado = pilarService.actualizar(id, pilarDTO);
        return ResponseEntity.ok(resultado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        pilarService.desactivar(id);
        return ResponseEntity.ok().build();
    }
}
