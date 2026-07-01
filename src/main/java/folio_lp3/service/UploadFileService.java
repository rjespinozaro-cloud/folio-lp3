package folio_lp3.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class UploadFileService {

    // Los archivos se guardarán en una carpeta llamada 'uploads' dentro de tus recursos estáticos
    private final String rootFolder = "src/main/resources/static/uploads/";

    public String guardarArchivo(MultipartFile archivo) throws IOException {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }

        // 1. Asegurar que el directorio perimetral exista en el servidor
        Path pathFolder = Paths.get(rootFolder);
        if (!Files.exists(pathFolder)) {
            Files.createDirectories(pathFolder);
        }

        // 2. Ofuscar el nombre original usando un UUID para prevenir colisiones o sobreescritura
        String nombreOriginal = archivo.getOriginalFilename();
        String extension = "";
        
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        } else {
            extension = ".bin"; // Extensión por defecto si no se detecta
        }
        
        String nuevoNombre = UUID.randomUUID().toString() + extension;

        // 3. Escribir el flujo de bytes del archivo en la ruta física local
        Path pathDestino = pathFolder.resolve(nuevoNombre);
        Files.copy(archivo.getInputStream(), pathDestino, StandardCopyOption.REPLACE_EXISTING);

        // 4. Retornar la URL relativa que el frontend del segundo personaje consumirá de forma estática
        return "/uploads/" + nuevoNombre;
    }
}