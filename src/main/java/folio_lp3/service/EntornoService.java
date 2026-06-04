package folio_lp3.service;

import folio_lp3.dto.EntornoDTO;
import folio_lp3.entity.Entorno;
import folio_lp3.repository.EntornoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para Entorno
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EntornoService {
    
    private final EntornoRepository entornoRepository;
    
    public EntornoDTO crearEntorno(EntornoDTO dto) {
        Entorno entorno = Entorno.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .build();
        
        Entorno guardado = entornoRepository.save(entorno);
        return convertirADTO(guardado);
    }
    
    public EntornoDTO obtenerPorId(Long id) {
        return entornoRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new RuntimeException("Entorno no encontrado"));
    }
    
    public List<EntornoDTO> listarTodos() {
        return entornoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public EntornoDTO actualizar(Long id, EntornoDTO dto) {
        Entorno entorno = entornoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entorno no encontrado"));
        
        entorno.setNombre(dto.getNombre());
        entorno.setDescripcion(dto.getDescripcion());
        
        Entorno actualizado = entornoRepository.save(entorno);
        return convertirADTO(actualizado);
    }
    
    public void eliminar(Long id) {
        entornoRepository.deleteById(id);
    }
    
    private EntornoDTO convertirADTO(Entorno entorno) {
        return EntornoDTO.builder()
                .id(entorno.getId())
                .nombre(entorno.getNombre())
                .descripcion(entorno.getDescripcion())
                .build();
    }
}
