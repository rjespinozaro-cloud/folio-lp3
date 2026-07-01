package folio_lp3.controller;

import folio_lp3.dto.DetalleComandoPilarDTO;
import folio_lp3.service.DetalleComandoPilarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para el desglose de comandos ejecutados y laboratorios del portafolio.
 */
@RestController
@RequestMapping("/api/v1/detalles-comandos")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.security.cors.allowed-origins:http://localhost:3000}")
public class DetalleComandoPilarController {
    
    private final DetalleComandoPilarService detalleService;
    
    // ===================================================================
    // PANEL 2 - ADMINISTRADOR
    // ===================================================================

    @PostMapping
    public ResponseEntity<DetalleComandoPilarDTO> crear(@RequestBody DetalleComandoPilarDTO dto) {
        DetalleComandoPilarDTO resultado = detalleService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DetalleComandoPilarDTO> actualizar(@PathVariable Long id, @RequestBody DetalleComandoPilarDTO dto) {
        DetalleComandoPilarDTO resultado = detalleService.actualizar(id, dto);
        return ResponseEntity.ok(resultado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detalleService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ===================================================================
    // PANEL 1 - RECLUTADOR / PÚBLICO
    // ===================================================================
    
    @GetMapping("/{id}")
    public ResponseEntity<DetalleComandoPilarDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(detalleService.obtenerPorId(id));
    }
    
    @GetMapping
    public ResponseEntity<List<DetalleComandoPilarDTO>> listarTodos() {
        return ResponseEntity.ok(detalleService.listarTodos());
    }
    
    @GetMapping("/pilar/{pilarId}")
    public ResponseEntity<List<DetalleComandoPilarDTO>> listarPorPilar(@PathVariable Long pilarId) {
        return ResponseEntity.ok(detalleService.listarPorPilar(pilarId));
    }
    
    @GetMapping("/herramienta/{herramientaId}")
    public ResponseEntity<List<DetalleComandoPilarDTO>> listarPorHerramienta(@PathVariable Long herramientaId) {
        return ResponseEntity.ok(detalleService.listarPorHerramienta(herramientaId));
    }
}