package folio_lp3.controller;

import folio_lp3.dto.ConsultaDTO;
import folio_lp3.enums.EstadoConsulta;
import folio_lp3.service.ConsultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para Consulta
 */
@RestController
@RequestMapping("/consultas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConsultaController {
    
    private final ConsultaService consultaService;
    
    @PostMapping
    public ResponseEntity<ConsultaDTO> crearConsulta(
            @RequestParam Long estudianteId,
            @RequestParam Long pilarId,
            @RequestParam String temaPrincipal) {
        ConsultaDTO resultado = consultaService.crearConsulta(estudianteId, pilarId, temaPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaDTO> obtenerPorId(@PathVariable Long id) {
        ConsultaDTO consulta = consultaService.obtenerPorId(id);
        return ResponseEntity.ok(consulta);
    }
    
    @GetMapping
    public ResponseEntity<List<ConsultaDTO>> listarTodas() {
        List<ConsultaDTO> consultas = consultaService.listarTodas();
        return ResponseEntity.ok(consultas);
    }
    
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<ConsultaDTO>> listarPorEstudiante(@PathVariable Long estudianteId) {
        List<ConsultaDTO> consultas = consultaService.listarPorEstudiante(estudianteId);
        return ResponseEntity.ok(consultas);
    }
    
    @GetMapping("/pilar/{pilarId}")
    public ResponseEntity<List<ConsultaDTO>> listarPorPilar(@PathVariable Long pilarId) {
        List<ConsultaDTO> consultas = consultaService.listarPorPilar(pilarId);
        return ResponseEntity.ok(consultas);
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ConsultaDTO>> listarPorEstado(@PathVariable String estado) {
        EstadoConsulta estadoEnum = EstadoConsulta.valueOf(estado.toUpperCase());
        List<ConsultaDTO> consultas = consultaService.listarPorEstado(estadoEnum);
        return ResponseEntity.ok(consultas);
    }
    
    @PutMapping("/{id}/estado")
    public ResponseEntity<ConsultaDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado) {
        EstadoConsulta estado = EstadoConsulta.valueOf(nuevoEstado.toUpperCase());
        ConsultaDTO resultado = consultaService.actualizarEstado(id, estado);
        return ResponseEntity.ok(resultado);
    }
    
    @PutMapping("/{id}/tokens")
    public ResponseEntity<ConsultaDTO> agregarTokens(
            @PathVariable Long id,
            @RequestParam Integer tokens) {
        ConsultaDTO resultado = consultaService.agregarTokens(id, tokens);
        return ResponseEntity.ok(resultado);
    }
    
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ConsultaDTO> cancelar(
            @PathVariable Long id,
            @RequestParam String motivo) {
        ConsultaDTO resultado = consultaService.cancelar(id, motivo);
        return ResponseEntity.ok(resultado);
    }
}
