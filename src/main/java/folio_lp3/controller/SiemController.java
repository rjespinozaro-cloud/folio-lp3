package folio_lp3.controller;

import folio_lp3.entity.SiemLog;
import folio_lp3.service.SiemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin") // 🛡️ Base: /api/v1/admin
@RequiredArgsConstructor
public class SiemController {

    private final SiemService siemService;

    // Retorna una lista plana ideal para la ráfaga visual del "LIVE CAPTURE"
    @GetMapping("/siem-logs")
    public ResponseEntity<List<SiemLog>> getLatestLogsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<SiemLog> logsPage = siemService.getLatestLogs(page, size);
        return ResponseEntity.ok(logsPage.getContent()); 
    }

    // 🚀 CORRECCIÓN: Endpoint de purga añadido para soportar la ruta '/api/v1/admin/auditoria/logs/purgar'
    @DeleteMapping("/auditoria/logs/purgar")
    public ResponseEntity<?> purgarLogsSiem() {
        siemService.purgarTodosLosLogs(); // Asegúrate de que este método exista en tu SiemService
        return ResponseEntity.ok(Map.of(
            "exitoso", true,
            "mensaje", "Registros del SIEM purgados correctamente."
        ));
    }
}