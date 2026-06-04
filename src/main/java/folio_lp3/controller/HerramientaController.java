package folio_lp3.controller;

import folio_lp3.dto.HerramientaDTO;
import folio_lp3.service.HerramientaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para Herramienta
 */
@RestController
@RequestMapping("/herramientas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HerramientaController {
    
    private final HerramientaService herramientaService;
    
    @PostMapping
    public ResponseEntity<HerramientaDTO> crear(@RequestBody HerramientaDTO dto) {
        HerramientaDTO resultado = herramientaService.crearHerramienta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<HerramientaDTO> obtenerPorId(@PathVariable Long id) {
        HerramientaDTO herramienta = herramientaService.obtenerPorId(id);
        return ResponseEntity.ok(herramienta);
    }
    
    @GetMapping
    public ResponseEntity<List<HerramientaDTO>> listarTodas() {
        List<HerramientaDTO> herramientas = herramientaService.listarTodas();
        return ResponseEntity.ok(herramientas);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<HerramientaDTO> actualizar(@PathVariable Long id, @RequestBody HerramientaDTO dto) {
        HerramientaDTO resultado = herramientaService.actualizar(id, dto);
        return ResponseEntity.ok(resultado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        herramientaService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
