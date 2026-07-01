package folio_lp3.controller;

import folio_lp3.entity.Certificaciones;
import folio_lp3.repository.CertificacionesRepository;
import folio_lp3.service.FileStorageService; // 👈 CAMBIADO: Usar el servicio unificado
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/certificaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CertificacionesController {

    private final CertificacionesRepository certificacionesRepository;
    private final FileStorageService fileStorageService; // 👈 CAMBIADO

    @GetMapping
    public ResponseEntity<List<Certificaciones>> listarTodas() {
        return ResponseEntity.ok(certificacionesRepository.findAll());
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> guardarCertificacion(
            @RequestParam("nombre") String nombre,
            @RequestParam("institucion") String institucion,
            @RequestParam(value = "codigoId", required = false) String codigoId,
            @RequestParam(value = "urlValidacion", required = false) String urlValidacion,
            @RequestParam(value = "badge", required = false) MultipartFile badge) {
        try {
            Certificaciones cert = new Certificaciones();
            cert.setNombre(nombre);
            cert.setInstitucion(institucion);
            cert.setCodigoId(codigoId);
            cert.setUrlValidacion(urlValidacion);

            if (badge != null && !badge.isEmpty()) {
                // 🛡️ UNIFICACIÓN: Usamos storeFile para guardar en el disco de verdad
                String fileName = fileStorageService.storeFile(badge); 
                
                cert.setRutaImagen("/api/v1/evidencias/download/" + fileName);
                cert.setImagenTipo(badge.getContentType());
            }

            return ResponseEntity.ok(certificacionesRepository.save(cert));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al procesar credencial: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> actualizarCertificacion(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("institucion") String institucion,
            @RequestParam(value = "codigoId", required = false) String codigoId,
            @RequestParam(value = "urlValidacion", required = false) String urlValidacion,
            @RequestParam(value = "badge", required = false) MultipartFile badge) {
        try {
            Optional<Certificaciones> certOptional = certificacionesRepository.findById(id);
            if (certOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Certificaciones cert = certOptional.get();
            cert.setNombre(nombre);
            cert.setInstitucion(institucion);
            cert.setCodigoId(codigoId);
            cert.setUrlValidacion(urlValidacion);

            if (badge != null && !badge.isEmpty()) {
                // 🛡️ UNIFICACIÓN AQUÍ TAMBIÉN
                String fileName = fileStorageService.storeFile(badge);
                
                cert.setRutaImagen("/api/v1/evidencias/download/" + fileName);
                cert.setImagenTipo(badge.getContentType());
            } 

            return ResponseEntity.ok(certificacionesRepository.save(cert));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar credencial: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCertificacion(@PathVariable Long id) {
        if (certificacionesRepository.existsById(id)) {
            certificacionesRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}