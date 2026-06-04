package folio_lp3.controller;

import folio_lp3.service.ReportesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller REST para Reportes y Estadísticas
 */
@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportesController {
    
    private final ReportesService reportesService;
    
    @GetMapping("/pilar/{pilarId}/mes/{mes}/year/{year}")
    public ResponseEntity<Map<String, Object>> obtenerReporteMensualPilar(
            @PathVariable Long pilarId,
            @PathVariable int mes,
            @PathVariable int year) {
        Map<String, Object> reporte = reportesService.obtenerReporteMensualPilar(pilarId, mes, year);
        return ResponseEntity.ok(reporte);
    }
    
    @GetMapping("/estudiantes-nuevos/mes/{mes}/year/{year}")
    public ResponseEntity<Map<String, Object>> obtenerNuevosEstudiantes(
            @PathVariable int mes,
            @PathVariable int year) {
        Long nuevos = reportesService.obtenerNuevosEstudiantes(mes, year);
        return ResponseEntity.ok(Map.of("mes", mes, "year", year, "nuevosEstudiantes", nuevos));
    }
    
    @GetMapping("/general")
    public ResponseEntity<Map<String, Object>> obtenerReporteGeneral() {
        Map<String, Object> reporte = reportesService.obtenerReporteGeneral();
        return ResponseEntity.ok(reporte);
    }
}
