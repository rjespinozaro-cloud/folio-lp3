package folio_lp3.controller;

import folio_lp3.entity.Practicas;
import folio_lp3.repository.PracticasRepository;
import folio_lp3.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

@RestController
@RequestMapping("/api/v1/practicas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PracticasController {

    private final PracticasRepository practicasRepository;
    private final UploadFileService uploadFileService;

    @GetMapping
    public ResponseEntity<List<Practicas>> listarTodas() {
        return ResponseEntity.ok(practicasRepository.findAll());
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> guardarPractica(
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo) {
        
        try {
            Practicas practica = new Practicas();
            practica.setTitulo(titulo);
            practica.setDescripcion(descripcion);
            practica.setCategoria(categoria);

            if (archivo != null && !archivo.isEmpty()) {
                String ruta = uploadFileService.guardarArchivo(archivo);
                practica.setRutaDocumento(ruta);
                practica.setDocumentoTipo(archivo.getContentType());
                
                // Extraemos y guardamos el Hash SHA-256 para el inventario de evidencias
                byte[] hashBytes = MessageDigest.getInstance("SHA-256").digest(archivo.getBytes());
                practica.setHashVerificacion(HexFormat.of().formatHex(hashBytes));
            }

            return ResponseEntity.ok(practicasRepository.save(practica));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error en persistencia de laboratorio: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> actualizarPractica(
            @PathVariable Long id,
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo) {
        
        return practicasRepository.findById(id).map(practicaExistente -> {
            try {
                practicaExistente.setTitulo(titulo);
                practicaExistente.setDescripcion(descripcion);
                practicaExistente.setCategoria(categoria);

                // Si el administrador subió un nuevo archivo para reemplazar el anterior
                if (archivo != null && !archivo.isEmpty()) {
                    String ruta = uploadFileService.guardarArchivo(archivo);
                    practicaExistente.setRutaDocumento(ruta);
                    practicaExistente.setDocumentoTipo(archivo.getContentType());
                    
                    // Recalculamos el Hash SHA-256 para la nueva evidencia criptográfica
                    byte[] hashBytes = MessageDigest.getInstance("SHA-256").digest(archivo.getBytes());
                    practicaExistente.setHashVerificacion(HexFormat.of().formatHex(hashBytes));
                }

                return ResponseEntity.ok(practicasRepository.save(practicaExistente));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error al actualizar la evidencia: " + e.getMessage());
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPractica(@PathVariable Long id) {
        if (practicasRepository.existsById(id)) {
            practicasRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}