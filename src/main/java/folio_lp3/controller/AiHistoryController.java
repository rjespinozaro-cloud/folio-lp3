package folio_lp3.controller;

import folio_lp3.entity.AiHistoryLog;
import folio_lp3.repository.AiHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/ai")
public class AiHistoryController {

    @Autowired
    private AiHistoryRepository aiHistoryRepository;

    @GetMapping("/history")
    public ResponseEntity<?> getAiHistory() {
        try {
            List<AiHistoryLog> logIA = aiHistoryRepository.findAllByOrderByTimestampDesc();
            return ResponseEntity.ok(logIA);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al extraer logs del Subsistema de IA");
        }
    }
}