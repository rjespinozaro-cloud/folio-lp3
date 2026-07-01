package folio_lp3.service;

import folio_lp3.dto.ConsultaDTO;
import folio_lp3.entity.Consulta;
import folio_lp3.entity.PilarCiberseguridad;
import folio_lp3.exception.AccesoDenegadoException;
import folio_lp3.entity.Usuario;
import folio_lp3.enums.EstadoConsulta;
import folio_lp3.enums.RolUsuario;
import folio_lp3.repository.ConsultaRepository;
import folio_lp3.repository.PilarCiberseguridadRepository;
import folio_lp3.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para Consulta - Lógica de negocio y Seguridad (Aislamiento de Datos)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ConsultaService {
    
    private final ConsultaRepository consultaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PilarCiberseguridadRepository pilarRepository;

    /**
     * Obtiene el usuario actualmente autenticado en el contexto de Spring Security.
     */
    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return null; // El Scheduler u operaciones internas retornarán null
        }
        return usuarioRepository.findByEmail(auth.getName()).orElse(null);
    }

    /**
     * Regla de Negocio: Aislamiento de Información.
     * Si el usuario es INSTRUCTOR, solo puede interactuar con consultas de SU Pilar.
     * Si el usuario es ESTUDIANTE, solo puede interactuar con SUS consultas.
     */
    private void validarAcceso(Consulta consulta) {
        Usuario currentUser = getUsuarioAutenticado();
        if (currentUser == null) return; // Permite procesos internos (Scheduler)

        if (currentUser.getRol() == RolUsuario.INSTRUCTOR) {
            if (currentUser.getPilarAsignado() == null || !currentUser.getPilarAsignado().getId().equals(consulta.getPilar().getId())) {
                throw new AccesoDenegadoException("Acceso Denegado: Esta consulta no pertenece a su pilar asignado.");
            }
        } else if (currentUser.getRol() == RolUsuario.ESTUDIANTE) {
            if (!currentUser.getId().equals(consulta.getEstudiante().getId())) {
                throw new AccesoDenegadoException("Acceso Denegado: No puede acceder a las consultas de otros estudiantes.");
            }
        }
    }

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
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
        
        validarAcceso(consulta); // Valida seguridad antes de retornar
        return convertirADTO(consulta);
    }
    
    public List<ConsultaDTO> listarTodas() {
        Usuario currentUser = getUsuarioAutenticado();
        List<Consulta> consultas;

        // Lógica de filtrado dinámico por Rol
        if (currentUser != null && currentUser.getRol() == RolUsuario.INSTRUCTOR) {
            Long pilarId = currentUser.getPilarAsignado() != null ? currentUser.getPilarAsignado().getId() : -1L;
            consultas = consultaRepository.findByPilarId(pilarId);
        } else if (currentUser != null && currentUser.getRol() == RolUsuario.ESTUDIANTE) {
            consultas = consultaRepository.findByEstudianteId(currentUser.getId());
        } else {
            consultas = consultaRepository.findAll(); // Administradores o Procesos
        }

        return consultas.stream().map(this::convertirADTO).collect(Collectors.toList());
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
        Usuario currentUser = getUsuarioAutenticado();
        List<Consulta> consultas;

        if (currentUser != null && currentUser.getRol() == RolUsuario.INSTRUCTOR) {
            Long pilarId = currentUser.getPilarAsignado() != null ? currentUser.getPilarAsignado().getId() : -1L;
            consultas = consultaRepository.findByEstadoAndPilarId(estado, pilarId);
        } else {
            consultas = consultaRepository.findByEstado(estado);
        }

        return consultas.stream().map(this::convertirADTO).collect(Collectors.toList());
    }
    
    // UPDATE
    public ConsultaDTO actualizarEstado(Long id, EstadoConsulta nuevoEstado) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
        
        validarAcceso(consulta);
        
        consulta.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoConsulta.ATENDIDA || nuevoEstado == EstadoConsulta.CANCELADA) {
            consulta.setFechaCierre(LocalDateTime.now());
        }
        
        Consulta actualizada = consultaRepository.save(consulta);
        return convertirADTO(actualizada);
    }
    
   // UPDATE OPTIMIZADO PARA TELEMETRÍA DE LA IA
    public ConsultaDTO agregarTokens(Long id, Integer tokens) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
        
        // Incremento directo en memoria antes de persistir
        consulta.setCantidadTokensUsados(consulta.getCantidadTokensUsados() + tokens);
        consulta.setUltimaActividad(LocalDateTime.now());
        consultaRepository.save(consulta);
        
        // Retornamos un DTO plano y rápido sin forzar la lectura del Lazy Loading de PreguntasIA
        return ConsultaDTO.builder()
                .id(consulta.getId())
                .cantidadTokensUsados(consulta.getCantidadTokensUsados())
                .estado(consulta.getEstado())
                .build();
    }
    
    public ConsultaDTO cancelar(Long id, String motivo) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
        
        validarAcceso(consulta);
        
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
        return tokens != null ? tokens : 0L;
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