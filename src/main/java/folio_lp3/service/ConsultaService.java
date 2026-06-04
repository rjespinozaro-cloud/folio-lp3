package folio_lp3.service;

import folio_lp3.dto.ConsultaDTO;
import folio_lp3.entity.Consulta;
import folio_lp3.entity.PilarCiberseguridad;
import folio_lp3.entity.Usuario;
import folio_lp3.enums.EstadoConsulta;
import folio_lp3.repository.ConsultaRepository;
import folio_lp3.repository.PilarCiberseguridadRepository;
import folio_lp3.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para Consulta - Lógica de negocio
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ConsultaService {
    
    private final ConsultaRepository consultaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PilarCiberseguridadRepository pilarRepository;
    
    // CREATE
    public ConsultaDTO crearConsulta(Long estudianteId, Long pilarId, String temaPrincipal) {
        Usuario estudiante = usuarioRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        PilarCiberseguridad pilar = pilarRepository.findById(pilarId)
                .orElseThrow(() -> new RuntimeException("Pilar no encontrado"));
        
        Consulta consulta = Consulta.builder()
                .estudiante(estudiante)
                .pilar(pilar)
                .temaPrincipal(temaPrincipal)
                .estado(EstadoConsulta.PENDIENTE)
                .cantidadTokensUsados(0)
                .build();
        
        Consulta guardada = consultaRepository.save(consulta);
        return convertirADTO(guardada);
    }
    
    // READ
    public ConsultaDTO obtenerPorId(Long id) {
        return consultaRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
    }
    
    public List<ConsultaDTO> listarPorEstudiante(Long estudianteId) {
        return consultaRepository.findByEstudianteId(estudianteId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public List<ConsultaDTO> listarPorPilar(Long pilarId) {
        return consultaRepository.findByPilarId(pilarId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public List<ConsultaDTO> listarPorEstado(EstadoConsulta estado) {
        return consultaRepository.findByEstado(estado).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public List<ConsultaDTO> listarTodas() {
        return consultaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // UPDATE
    public ConsultaDTO actualizarEstado(Long id, EstadoConsulta nuevoEstado) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
        
        consulta.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoConsulta.ATENDIDA || nuevoEstado == EstadoConsulta.CANCELADA) {
            consulta.setFechaCierre(LocalDateTime.now());
        }
        
        Consulta actualizada = consultaRepository.save(consulta);
        return convertirADTO(actualizada);
    }
    
    public ConsultaDTO agregarTokens(Long id, Integer tokens) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
        
        consulta.setCantidadTokensUsados(consulta.getCantidadTokensUsados() + tokens);
        consulta.setUltimaActividad(LocalDateTime.now());
        
        Consulta actualizada = consultaRepository.save(consulta);
        return convertirADTO(actualizada);
    }
    
    public ConsultaDTO cancelar(Long id, String motivo) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
        
        consulta.setEstado(EstadoConsulta.CANCELADA);
        consulta.setMotivoCancelacion(motivo);
        consulta.setFechaCierre(LocalDateTime.now());
        
        Consulta actualizada = consultaRepository.save(consulta);
        return convertirADTO(actualizada);
    }
    
    // REPORTES
    public Long obtenerConsultasAtendidas(Long pilarId, int mes, int year) {
        return consultaRepository.countAttendedByPilarAndMonth(pilarId, mes, year);
    }
    
    public Long obtenerConsultasCanceladas(Long pilarId, int mes, int year) {
        return consultaRepository.countCancelledByPilarAndMonth(pilarId, mes, year);
    }
    
    public Long obtenerTokensPorPilar(Long pilarId, int mes, int year) {
        Long tokens = consultaRepository.sumTokensByPilarAndMonth(pilarId, mes, year);
        return tokens != null ? tokens : 0;
    }
    
    private ConsultaDTO convertirADTO(Consulta consulta) {
        return ConsultaDTO.builder()
                .id(consulta.getId())
                .estudianteId(consulta.getEstudiante().getId())
                .estudianteNombre(consulta.getEstudiante().getNombreCompleto())
                .pilarId(consulta.getPilar().getId())
                .pilarNombre(consulta.getPilar().getNombrePilar())
                .temaPrincipal(consulta.getTemaPrincipal())
                .estado(consulta.getEstado())
                .cantidadTokensUsados(consulta.getCantidadTokensUsados())
                .fechaCreacion(consulta.getFechaCreacion())
                .ultimaActividad(consulta.getUltimaActividad())
                .fechaCierre(consulta.getFechaCierre())
                .motivoCancelacion(consulta.getMotivoCancelacion())
                .totalPreguntasIA(consulta.getPreguntasIA().size())
                .build();
    }
}
