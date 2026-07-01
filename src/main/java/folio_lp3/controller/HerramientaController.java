package folio_lp3.controller;

import folio_lp3.dto.HerramientaDTO;
import folio_lp3.dto.DetalleComandoPilarDTO;
import folio_lp3.dto.PilarCiberseguridadDTO;
import folio_lp3.service.HerramientaService;
import folio_lp3.service.PilarCiberseguridadService;
import folio_lp3.service.FileStorageService; 
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/herramientas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HerramientaController {
    
    private final HerramientaService herramientaService;
    private final PilarCiberseguridadService pilarCiberseguridadService;
    private final FileStorageService fileStorageService;

    // ===================================================================
    // METADATOS: CARGA DINÁMICA DESDE MARIADB
    // ===================================================================
    @GetMapping("/opciones-formulario")
    public ResponseEntity<?> getFormOptions() {
        try {
            List<PilarCiberseguridadDTO> pilares = pilarCiberseguridadService.listarActivos();
            List<String> enfoques = herramientaService.obtenerEnfoquesOperativosExistentes(); 

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("pilares", pilares);
            respuesta.put("enfoques", enfoques);

            return ResponseEntity.ok(respuesta);
        } catch (Exception error) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error de infraestructura al leer las tablas de configuración."));
        }
    }

    /**
     * DICCIONARIO EN CALIENTE [ + ]: Guarda una nueva Disciplina / Pilar de Ciberseguridad
     */
    @PostMapping("/pilares")
    public ResponseEntity<?> agregarNuevaDisciplina(@RequestBody PilarCiberseguridadDTO pilarDTO) {
        try {
            // Aseguramos valores por defecto requeridos por tus validaciones @NotBlank
            if (pilarDTO.getNombreInstructor() == null) {
                pilarDTO.setNombreInstructor("SEC-ADMIN");
            }
            if (pilarDTO.getCorreoContacto() == null) {
                pilarDTO.setCorreoContacto("admin@cyberportfolio.local");
            }
            
            PilarCiberseguridadDTO guardado = pilarCiberseguridadService.guardar(pilarDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (Exception error) {
            System.err.println("❌ Fallo al insertar nuevo pilar: " + error.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "No se pudo registrar la nueva disciplina en MariaDB."));
        }
    }

    /**
     * DICCIONARIO EN CALIENTE [ + ]: Registra un nuevo Enfoque Operativo de comandos
     */
    @PostMapping("/enfoques")
    public ResponseEntity<?> agregarNuevoEnfoque(@RequestBody Map<String, String> payload) {
        try {
            String nuevoEnfoque = payload.get("tipoComando");
            if (nuevoEnfoque == null || nuevoEnfoque.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "El tipo de comando no puede estar vacío."));
            }
            
            // Registra el enfoque delegando en el servicio (puedes persistirlo en una entidad o asignarlo dinámicamente)
            herramientaService.guardarNuevoEnfoqueOperativo(nuevoEnfoque.trim().toUpperCase());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("tipoComando", nuevoEnfoque.toUpperCase()));
        } catch (Exception error) {
            System.err.println("❌ Fallo al insertar nuevo enfoque: " + error.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "No se pudo registrar el nuevo enfoque operativo."));
        }
    }

    // ===================================================================
    // GUARDAR O ACTUALIZAR CON ARCHIVO (MULTIPART TRAN SACCIONAL)
    // ===================================================================
    @PostMapping(value = "/con-archivo", consumes = {"multipart/form-data"})
    public ResponseEntity<HerramientaDTO> guardarOActualizarConArchivo(
            @RequestParam(value = "id", required = false) Long id, 
            @RequestParam("nombre") String nombre,
            @RequestParam("porcentaje") Integer porcentaje,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "pilarId", required = false) Long pilarId,
            @RequestParam(value = "tipoComando", required = false) String tipoComando,
            @RequestParam(value = "sintaxis", required = false) String sintaxis,
            @RequestParam(value = "vulnerabilidadAsociada", required = false) String vulnerabilidadAsociada,
            @RequestParam(value = "mitigacion", required = false) String mitigacion,
            @RequestParam(value = "descripcionPersonalizada", required = false) String descripcionPersonalizada,
            @RequestParam(value = "subtemaId", required = false) Long subtemaId) {
        
        String rutaEvidencia = null; 
        
        if (archivo != null && !archivo.isEmpty()) {
            try {
                String fileName = fileStorageService.storeFile(archivo);
                rutaEvidencia = "/api/v1/evidencias/download/" + fileName;
            } catch (Exception e) {
                System.err.println("❌ Fallo en FileStorageService: " + e.getMessage());
            }
        } else if (id != null) {
            try {
                HerramientaDTO herramientaExistente = herramientaService.obtenerPorId(id);
                rutaEvidencia = herramientaExistente.getUrlDocumentacion();
            } catch (Exception e) {
                rutaEvidencia = null;
            }
        }

        HerramientaDTO dto = HerramientaDTO.builder()
                .id(id) 
                .nombre(nombre)
                .nivelDificultad(porcentaje + "%") 
                .urlDocumentacion(rutaEvidencia) 
                .descripcion(descripcion != null ? descripcion : "Módulo operativo del arsenal de ciberseguridad.")
                .creador("SEC-ADMIN")
                .build();

        DetalleComandoPilarDTO comandoDto = null;
        if (sintaxis != null && !sintaxis.trim().isEmpty()) {
            comandoDto = DetalleComandoPilarDTO.builder()
                    .pilarId(pilarId != null ? pilarId : 1L) 
                    .tipoComando(tipoComando)
                    .sintaxis(sintaxis)
                    .vulnerabilidadAsociada(vulnerabilidadAsociada)
                    .mitigacion(mitigacion)
                    .descripcionPersonalizada(descripcionPersonalizada)
                    .subtemaId(subtemaId)
                    .activo(true)
                    .build();
        }

        HerramientaDTO resultado = herramientaService.crearHerramientaConComando(dto, comandoDto);
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
    }

    // Adición de soporte para ediciones directas por PUT invocadas desde el Frontend
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<HerramientaDTO> actualizarHerramienta(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("porcentaje") Integer porcentaje,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "pilarId", required = false) Long pilarId,
            @RequestParam(value = "tipoComando", required = false) String tipoComando,
            @RequestParam(value = "sintaxis", required = false) String sintaxis,
            @RequestParam(value = "vulnerabilidadAsociada", required = false) String vulnerabilidadAsociada,
            @RequestParam(value = "mitigacion", required = false) String mitigacion,
            @RequestParam(value = "descripcionPersonalizada", required = false) String descripcionPersonalizada,
            @RequestParam(value = "subtemaId", required = false) Long subtemaId) {
        
        return guardarOActualizarConArchivo(id, nombre, porcentaje, archivo, descripcion, pilarId, tipoComando, sintaxis, vulnerabilidadAsociada, mitigacion, descripcionPersonalizada, subtemaId);
    }

    // ===================================================================
    // ENDPOINTS CRUD ESTÁNDAR
    // ===================================================================
    @GetMapping("/{id}")
    public ResponseEntity<HerramientaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(herramientaService.obtenerPorId(id));
    }
    
    @GetMapping
    public ResponseEntity<List<HerramientaDTO>> listarTodas() {
        return ResponseEntity.ok(herramientaService.listarTodas());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        herramientaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}