package folio_lp3.service;

import folio_lp3.dto.PilarCiberseguridadDTO;
import folio_lp3.entity.Entorno;
import folio_lp3.entity.PilarCiberseguridad;
import folio_lp3.repository.EntornoRepository;
import folio_lp3.repository.PilarCiberseguridadRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PilarCiberseguridadService {
    
    private final PilarCiberseguridadRepository pilarRepository;
    private final EntornoRepository entornoRepository;
    
    @Transactional
    public PilarCiberseguridadDTO crearPilar(PilarCiberseguridadDTO dto) {
        Entorno entorno = null;
        if (dto.getEntornoId() != null) {
            entorno = entornoRepository.findById(dto.getEntornoId())
                    .orElseThrow(() -> new EntityNotFoundException("El entorno con ID " + dto.getEntornoId() + " no existe."));
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
        
        return convertirADTO(pilarRepository.save(pilar));
    }

    /**
     * SOPORTE PARA BOTÓN [ + ] DESDE EL CONTROLLER: Guarda un pilar/disciplina en caliente
     */
    @Transactional
    public PilarCiberseguridadDTO guardar(PilarCiberseguridadDTO dto) {
        Entorno entornoDefault = null;
        if (dto.getEntornoId() != null) {
            entornoDefault = entornoRepository.findById(dto.getEntornoId()).orElse(null);
        } else {
            // Fallback: Si no se pasa un entorno, se asocia al primer entorno del sistema si existe
            entornoDefault = entornoRepository.findAll().stream().findFirst().orElse(null);
        }

        PilarCiberseguridad pilar = PilarCiberseguridad.builder()
                .nombrePilar(dto.getNombrePilar().toUpperCase())
                .nombreInstructor(dto.getNombreInstructor() != null ? dto.getNombreInstructor() : "SEC-ADMIN")
                .correoContacto(dto.getCorreoContacto() != null ? dto.getCorreoContacto() : "admin@cyberportfolio.local")
                .iconoUrl(dto.getIconoUrl())
                .temario(dto.getTemario())
                .activo(true)
                .entorno(entornoDefault)
                .build();

        return convertirADTO(pilarRepository.save(pilar));
    }
    
    @Transactional(readOnly = true)
    public PilarCiberseguridadDTO obtenerPorId(Long id) {
        return pilarRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new EntityNotFoundException("El pilar de seguridad con ID " + id + " no fue localizado."));
    }
    
    @Transactional(readOnly = true)
    public List<PilarCiberseguridadDTO> listarTodos() {
        return pilarRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PilarCiberseguridadDTO> listarActivos() {
        return pilarRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PilarCiberseguridadDTO> listarPorEntorno(Long entornoId) {
        return pilarRepository.findActiveByEntorno(entornoId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PilarCiberseguridadDTO actualizar(Long id, PilarCiberseguridadDTO dto) {
        PilarCiberseguridad pilar = pilarRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede actualizar. Pilar ID " + id + " no existe."));
        
        pilar.setNombrePilar(dto.getNombrePilar());
        pilar.setNombreInstructor(dto.getNombreInstructor());
        pilar.setCorreoContacto(dto.getCorreoContacto());
        pilar.setTemario(dto.getTemario());
        pilar.setEnlacesReferencia(dto.getEnlacesReferencia());
        pilar.setUrlRepositorio(dto.getUrlRepositorio());
        pilar.setHorarioTutoriaInicio(dto.getHorarioTutoriaInicio());
        pilar.setHorarioTutoriaFin(dto.getHorarioTutoriaFin());
        
        return convertirADTO(pilarRepository.save(pilar));
    }
    
    @Transactional
    public void desactivar(Long id) {
        PilarCiberseguridad pilar = pilarRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede desactivar. Pilar ID " + id + " no existe."));
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
                .totalConsultas(pilar.getConsultas() != null ? pilar.getConsultas().size() : 0)
                .build();
    }
}