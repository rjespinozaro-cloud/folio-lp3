package folio_lp3.service;

import folio_lp3.dto.PilarCiberseguridadDTO;
import folio_lp3.entity.Entorno;
import folio_lp3.entity.PilarCiberseguridad;
import folio_lp3.repository.EntornoRepository;
import folio_lp3.repository.PilarCiberseguridadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para PilarCiberseguridad
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PilarCiberseguridadService {
    
    private final PilarCiberseguridadRepository pilarRepository;
    private final EntornoRepository entornoRepository;
    
    // CREATE
    public PilarCiberseguridadDTO crearPilar(PilarCiberseguridadDTO dto) {
        Entorno entorno = null;
        if (dto.getEntornoId() != null) {
            entorno = entornoRepository.findById(dto.getEntornoId())
                    .orElseThrow(() -> new RuntimeException("Entorno no encontrado"));
        }
        
        PilarCiberseguridad pilar = PilarCiberseguridad.builder()
                .nombrePilar(dto.getNombrePilar())
                .nombreInstructor(dto.getNombreInstructor())
                .correoContacto(dto.getCorreoContacto())
                .iconoUrl(dto.getIconoUrl())
                .temario(dto.getTemario())
                .enlacesReferencia(dto.getEnlacesReferencia())
                .urlRepositorio(dto.getUrlRepositorio())
                .horarioTutoriaInicio(dto.getHorarioTutoriaInicio())
                .horarioTutoriaFin(dto.getHorarioTutoriaFin())
                .entorno(entorno)
                .activo(true)
                .build();
        
        PilarCiberseguridad guardado = pilarRepository.save(pilar);
        return convertirADTO(guardado);
    }
    
    // READ
    public PilarCiberseguridadDTO obtenerPorId(Long id) {
        return pilarRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new RuntimeException("Pilar no encontrado"));
    }
    
    public List<PilarCiberseguridadDTO> listarTodos() {
        return pilarRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public List<PilarCiberseguridadDTO> listarActivos() {
        return pilarRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public List<PilarCiberseguridadDTO> listarPorEntorno(Long entornoId) {
        return pilarRepository.findActiveByEntorno(entornoId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // UPDATE
    public PilarCiberseguridadDTO actualizar(Long id, PilarCiberseguridadDTO dto) {
        PilarCiberseguridad pilar = pilarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pilar no encontrado"));
        
        pilar.setNombrePilar(dto.getNombrePilar());
        pilar.setNombreInstructor(dto.getNombreInstructor());
        pilar.setCorreoContacto(dto.getCorreoContacto());
        pilar.setTemario(dto.getTemario());
        pilar.setEnlacesReferencia(dto.getEnlacesReferencia());
        pilar.setUrlRepositorio(dto.getUrlRepositorio());
        pilar.setHorarioTutoriaInicio(dto.getHorarioTutoriaInicio());
        pilar.setHorarioTutoriaFin(dto.getHorarioTutoriaFin());
        
        PilarCiberseguridad actualizado = pilarRepository.save(pilar);
        return convertirADTO(actualizado);
    }
    
    // DELETE
    public void desactivar(Long id) {
        PilarCiberseguridad pilar = pilarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pilar no encontrado"));
        pilar.setActivo(false);
        pilarRepository.save(pilar);
    }
    
    private PilarCiberseguridadDTO convertirADTO(PilarCiberseguridad pilar) {
        return PilarCiberseguridadDTO.builder()
                .id(pilar.getId())
                .nombrePilar(pilar.getNombrePilar())
                .nombreInstructor(pilar.getNombreInstructor())
                .correoContacto(pilar.getCorreoContacto())
                .iconoUrl(pilar.getIconoUrl())
                .temario(pilar.getTemario())
                .enlacesReferencia(pilar.getEnlacesReferencia())
                .urlRepositorio(pilar.getUrlRepositorio())
                .horarioTutoriaInicio(pilar.getHorarioTutoriaInicio())
                .horarioTutoriaFin(pilar.getHorarioTutoriaFin())
                .entornoId(pilar.getEntorno() != null ? pilar.getEntorno().getId() : null)
                .entornoNombre(pilar.getEntorno() != null ? pilar.getEntorno().getNombre() : null)
                .activo(pilar.getActivo())
                .totalConsultas(pilar.getConsultas().size())
                .build();
    }
}
