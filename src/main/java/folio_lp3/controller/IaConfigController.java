package folio_lp3.controller;

import folio_lp3.dto.IaConfigRequestDTO;
import folio_lp3.service.IaConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/configuracion-ia")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class IaConfigController {

    private final IaConfigService iaConfigService;

    /**
     * Endpoint modificado para listar todos los motores cargados en la tabla CRUD
     */
    @GetMapping
    public ResponseEntity<List<IaConfigRequestDTO>> obtenerConfiguracion() {
        try {
            log.info("📡 Solicitud recibida: Listando registros del Motor RAG para la tabla...");
            List<IaConfigRequestDTO> lista = iaConfigService.listarTodasConfiguraciones();
            return ResponseEntity.ok(lista);
            
        } catch (Exception e) {
            log.error("❌ Error al listar las configuraciones de IA: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint para guardar o actualizar un registro desde la tabla CRUD (PUT)
     */
    @PutMapping
    public ResponseEntity<IaConfigRequestDTO> actualizarConfiguracion(@RequestBody IaConfigRequestDTO requestDTO) {
        try {
            log.info("⚙️ Solicitud recibida: Guardando/Actualizando fila de configuración IA...");
            IaConfigRequestDTO configActualizada = iaConfigService.actualizarConfiguracion(requestDTO);
            return ResponseEntity.ok(configActualizada);
            
        } catch (Exception e) {
            log.error("❌ Fallo crítico al procesar la configuración de IA: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint para eliminar un motor de IA específico por su ID (DELETE)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarConfiguracion(@PathVariable Long id) {
        try {
            log.info("🗑️ Solicitud recibida: Eliminando motor de IA con ID: {}", id);
            // Asegúrate de tener implementado el método 'eliminarConfiguracion(id)' en tu IaConfigService
            iaConfigService.eliminarConfiguracion(id); 
            return ResponseEntity.noContent().build(); // Retorna un 204 No Content si se eliminó con éxito
            
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ No se pudo eliminar: {}", e.getMessage());
            return ResponseEntity.notFound().build(); // Retorna 404 si el ID no existía
        } catch (Exception e) {
            log.error("❌ Error crítico al eliminar la configuración de IA: {}", e.getMessage());
            return ResponseEntity.internalServerError().build(); // Retorna 500 si pasa algo en la BD
        }
    }
}