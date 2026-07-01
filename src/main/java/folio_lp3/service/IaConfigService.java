package folio_lp3.service;

import folio_lp3.dto.IaConfigRequestDTO;
import folio_lp3.entity.IaConfig;
import folio_lp3.repository.IaConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IaConfigService {

    private final IaConfigRepository iaConfigRepository;

    /**
     * Devuelve TODOS los motores de IA registrados para poblar la tabla.
     */
    @Transactional(readOnly = true)
    public List<IaConfigRequestDTO> listarTodasConfiguraciones() {
        return iaConfigRepository.findAll().stream()
                .map(config -> IaConfigRequestDTO.builder()
                        .id(config.getId())
                        .proveedorActivo(config.getProveedorActivo())
                        .nombreModelo(config.getNombreModelo())
                        .geminiApiKey(config.getGeminiApiKey())
                        .ollamaUrl(config.getOllamaUrl())
                        .systemPrompt(config.getSystemPrompt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Guarda un nuevo motor o actualiza uno existente basándose en el ID enviado.
     */
    @Transactional
    public IaConfigRequestDTO actualizarConfiguracion(IaConfigRequestDTO requestDTO) {
        IaConfig config;

        // Si viene un ID del frontend, buscamos esa fila exacta para editarla
        if (requestDTO.getId() != null) {
            config = iaConfigRepository.findById(requestDTO.getId())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró la configuración con ID: " + requestDTO.getId()));
        } else {
            // Si el ID viene nulo o vacío, es un registro totalmente nuevo en la tabla
            config = new IaConfig();
        }

        // Mapeo riguroso de parámetros hacia la Entidad
        config.setProveedorActivo(requestDTO.getProveedorActivo());
        config.setNombreModelo(requestDTO.getNombreModelo());
        config.setGeminiApiKey(requestDTO.getGeminiApiKey());
        config.setOllamaUrl(requestDTO.getOllamaUrl());
        config.setSystemPrompt(requestDTO.getSystemPrompt());

        IaConfig configGuardada = iaConfigRepository.save(config);
        log.info("✅ Configuración de IA [{}] persistida con éxito en BD.", configGuardada.getNombreModelo());

        // Respuesta armada con el ID correspondiente devuelto por Hibernate
        return IaConfigRequestDTO.builder()
                .id(configGuardada.getId())
                .proveedorActivo(configGuardada.getProveedorActivo())
                .nombreModelo(configGuardada.getNombreModelo())
                .geminiApiKey(configGuardada.getGeminiApiKey())
                .ollamaUrl(configGuardada.getOllamaUrl())
                .systemPrompt(configGuardada.getSystemPrompt())
                .build();
    }

    /**
     * Elimina una configuración de IA específica por su ID.
     */
    @Transactional
    public void eliminarConfiguracion(Long id) {
        // Validamos si existe en la BD antes de proceder al borrado
        boolean existe = iaConfigRepository.existsById(id);
        if (!existe) {
            throw new IllegalArgumentException("No se puede eliminar. No existe configuración de IA con ID: " + id);
        }
        
        iaConfigRepository.deleteById(id);
        log.info("🗑️ Registro de configuración de IA con ID [{}] eliminado con éxito de la BD.", id);
    }
}