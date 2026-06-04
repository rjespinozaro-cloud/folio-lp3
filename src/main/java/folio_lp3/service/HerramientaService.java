package folio_lp3.service;

import folio_lp3.dto.HerramientaDTO;
import folio_lp3.entity.Herramienta;
import folio_lp3.repository.HerramientaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para Herramienta
 */
@Service
@RequiredArgsConstructor
@Transactional
public class HerramientaService {
    
    private final HerramientaRepository herramientaRepository;
    
    public HerramientaDTO crearHerramienta(HerramientaDTO dto) {
        Herramienta herramienta = Herramienta.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .creador(dto.getCreador())
                .nivelDificultad(dto.getNivelDificultad())
                .urlDocumentacion(dto.getUrlDocumentacion())
                .build();
        
        Herramienta guardada = herramientaRepository.save(herramienta);
        return convertirADTO(guardada);
    }
    
    public HerramientaDTO obtenerPorId(Long id) {
        return herramientaRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada"));
    }
    
    public List<HerramientaDTO> listarTodas() {
        return herramientaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public HerramientaDTO actualizar(Long id, HerramientaDTO dto) {
        Herramienta herramienta = herramientaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada"));
        
        herramienta.setNombre(dto.getNombre());
        herramienta.setDescripcion(dto.getDescripcion());
        herramienta.setCreador(dto.getCreador());
        herramienta.setNivelDificultad(dto.getNivelDificultad());
        herramienta.setUrlDocumentacion(dto.getUrlDocumentacion());
        
        Herramienta actualizada = herramientaRepository.save(herramienta);
        return convertirADTO(actualizada);
    }
    
    public void eliminar(Long id) {
        herramientaRepository.deleteById(id);
    }
    
    private HerramientaDTO convertirADTO(Herramienta herramienta) {
        return HerramientaDTO.builder()
                .id(herramienta.getId())
                .nombre(herramienta.getNombre())
                .descripcion(herramienta.getDescripcion())
                .creador(herramienta.getCreador())
                .nivelDificultad(herramienta.getNivelDificultad())
                .urlDocumentacion(herramienta.getUrlDocumentacion())
                .build();
    }
}
