package folio_lp3.controller;

import folio_lp3.dto.PilarCiberseguridadDTO;
import folio_lp3.service.PilarCiberseguridadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para la gestión de Especialidades/Pilares de Ciberseguridad.
 * Aplica CORS restrictivo y prepara los contextos para accesos diferenciados.
 */
@RestController
@RequestMapping("/api/v1/pilares")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.security.cors.allowed-origins:http://localhost:3000}")
public class PilarCiberseguridadController {
    
    private final PilarCiberseguridadService pilarService;
    
    // ===================================================================
    // PANEL 2 - ADMINISTRADOR (EXCLUSIVO CON AUTENTICACIÓN)
    // ===================================================================

    @PostMapping
    public ResponseEntity<PilarCiberseguridadDTO> crearPilar(@RequestBody PilarCiberseguridadDTO pilarDTO) {
        PilarCiberseguridadDTO resultado = pilarService.crearPilar(pilarDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
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
        return ResponseEntity.noContent().build();
    }

    // ===================================================================
    // PANEL 1 - RECLUTADOR / PÚBLICO (ACCESO LIBRE READ-ONLY)
    // ===================================================================
    
    @GetMapping("/{id}")
    public ResponseEntity<PilarCiberseguridadDTO> obtenerPorId(@PathVariable Long id) {
        // CORRECCIÓN: Cambiado obtainPorId por obtenerPorId para hacer match con el Service
        return ResponseEntity.ok(pilarService.obtenerPorId(id));
    }
    
    @GetMapping
    public ResponseEntity<List<PilarCiberseguridadDTO>> listarTodos() {
        return ResponseEntity.ok(pilarService.listarTodos());
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<PilarCiberseguridadDTO>> listarActivos() {
        return ResponseEntity.ok(pilarService.listarActivos());
    }
    
    @GetMapping("/entorno/{entornoId}")
    public ResponseEntity<List<PilarCiberseguridadDTO>> listarPorEntorno(@PathVariable Long entornoId) {
        return ResponseEntity.ok(pilarService.listarPorEntorno(entornoId));
    }
}