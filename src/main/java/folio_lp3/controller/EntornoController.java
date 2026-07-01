package folio_lp3.controller;

import folio_lp3.dto.EntornoDTO;
import folio_lp3.service.EntornoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para Entorno
 */
@RestController
@RequestMapping("/api/v1/entornos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EntornoController {
    
    private final EntornoService entornoService;
    
    @PostMapping
    public ResponseEntity<EntornoDTO> crear(@RequestBody EntornoDTO dto) {
        EntornoDTO resultado = entornoService.crearEntorno(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntornoDTO> obtenerPorId(@PathVariable Long id) {
        EntornoDTO entorno = entornoService.obtenerPorId(id);
        return ResponseEntity.ok(entorno);
    }
    
    @GetMapping
    public ResponseEntity<List<EntornoDTO>> listarTodos() {
        List<EntornoDTO> entornos = entornoService.listarTodos();
        return ResponseEntity.ok(entornos);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EntornoDTO> actualizar(@PathVariable Long id, @RequestBody EntornoDTO dto) {
        EntornoDTO resultado = entornoService.actualizar(id, dto);
        return ResponseEntity.ok(resultado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        entornoService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
