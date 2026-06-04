package folio_lp3.controller;

import folio_lp3.dto.DetalleComandoPilarDTO;
import folio_lp3.service.DetalleComandoPilarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para Detalle de Comando de Pilar
 */
@RestController
@RequestMapping("/detalles-comandos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DetalleComandoPilarController {
    
    private final DetalleComandoPilarService detalleService;
    
    @PostMapping
    public ResponseEntity<DetalleComandoPilarDTO> crear(@RequestBody DetalleComandoPilarDTO dto) {
        DetalleComandoPilarDTO resultado = detalleService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DetalleComandoPilarDTO> obtenerPorId(@PathVariable Long id) {
        DetalleComandoPilarDTO detalle = detalleService.obtenerPorId(id);
        return ResponseEntity.ok(detalle);
    }
    
    @GetMapping
    public ResponseEntity<List<DetalleComandoPilarDTO>> listarTodos() {
        List<DetalleComandoPilarDTO> detalles = detalleService.listarTodos();
        return ResponseEntity.ok(detalles);
    }
    
    @GetMapping("/pilar/{pilarId}")
    public ResponseEntity<List<DetalleComandoPilarDTO>> listarPorPilar(@PathVariable Long pilarId) {
        List<DetalleComandoPilarDTO> detalles = detalleService.listarPorPilar(pilarId);
        return ResponseEntity.ok(detalles);
    }
    
    @GetMapping("/herramienta/{herramientaId}")
    public ResponseEntity<List<DetalleComandoPilarDTO>> listarPorHerramienta(@PathVariable Long herramientaId) {
        List<DetalleComandoPilarDTO> detalles = detalleService.listarPorHerramienta(herramientaId);
        return ResponseEntity.ok(detalles);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DetalleComandoPilarDTO> actualizar(@PathVariable Long id, @RequestBody DetalleComandoPilarDTO dto) {
        DetalleComandoPilarDTO resultado = detalleService.actualizar(id, dto);
        return ResponseEntity.ok(resultado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detalleService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
