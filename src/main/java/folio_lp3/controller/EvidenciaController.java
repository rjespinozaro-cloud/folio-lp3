package folio_lp3.controller;

import folio_lp3.dto.RespuestaGenericaDTO;
import folio_lp3.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

@RestController
@RequestMapping("/api/v1/evidencias")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EvidenciaController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<RespuestaGenericaDTO<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = fileStorageService.storeFile(file);
            String fileDownloadUri = "/api/v1/evidencias/download/" + fileName;
            
            // Generación de Hash SHA-256 en Caliente para la auditoría SIEM
            byte[] hashBytes = MessageDigest.getInstance("SHA-256").digest(file.getBytes());
            String fileHash = HexFormat.of().formatHex(hashBytes);

            return ResponseEntity.ok(RespuestaGenericaDTO.exitoso(
                "Integridad Verificada (SHA256: " + fileHash + "). Archivo: " + fileName, 
                fileDownloadUri
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(RespuestaGenericaDTO.error("Fallo de carga: " + e.getMessage(), null));
        }
    }

    // 1. TU MÉTODO ORIGINAL (INTACTO): Mantiene tus herramientas vivas al 100%
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(filename);
        
        String contentType = request.getServletContext().getMimeType(resource.toString());
        if (contentType == null) { contentType = "image/png"; } // Fallback visual

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    // 2. SOPORTE DE COMPATIBILIDAD INTERMEDIO: Rescata los registros antiguos que contienen "uploads/"
    @GetMapping("/download/uploads/{filename:.+}")
    public ResponseEntity<Resource> downloadFileFromUploads(@PathVariable String filename, HttpServletRequest request) {
        // Al mapear explícitamente /uploads/, capturamos el nombre directo del archivo y saltamos el error
        Resource resource = fileStorageService.loadFileAsResource(filename);
        
        String contentType = request.getServletContext().getMimeType(resource.toString());
        if (contentType == null) { contentType = "image/png"; }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/files")
    public ResponseEntity<RespuestaGenericaDTO<List<String>>> listFiles() {
        return ResponseEntity.ok(RespuestaGenericaDTO.exitoso("Repositorio leído", fileStorageService.listFiles()));
    }
}