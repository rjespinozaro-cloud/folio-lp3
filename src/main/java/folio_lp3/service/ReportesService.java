package folio_lp3.service;

import folio_lp3.repository.ConsultaRepository;
import folio_lp3.repository.PreguntaIARepository;
import folio_lp3.repository.UsuarioRepository;
import folio_lp3.enums.RolUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de Reportes - Estadísticas y análisis
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportesService {
    
    private final ConsultaRepository consultaRepository;
    private final PreguntaIARepository preguntaRepository;
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Reporte mensual de un pilar específico
     */
    public Map<String, Object> obtenerReporteMensualPilar(Long pilarId, int mes, int year) {
        Map<String, Object> reporte = new HashMap<>();
        
        Long atendidas = consultaRepository.countAttendedByPilarAndMonth(pilarId, mes, year);
        Long canceladas = consultaRepository.countCancelledByPilarAndMonth(pilarId, mes, year);
        Long tokens = consultaRepository.sumTokensByPilarAndMonth(pilarId, mes, year);
        Long respuestasMalas = preguntaRepository.countBadRatingsByPilarAndMonth(pilarId, mes, year);
        
        reporte.put("pilarId", pilarId);
        reporte.put("mes", mes);
        reporte.put("year", year);
        reporte.put("consultasAtendidas", atendidas != null ? atendidas : 0);
        reporte.put("consultasCanceladas", canceladas != null ? canceladas : 0);
        reporte.put("tokensConsumidos", tokens != null ? tokens : 0);
        reporte.put("respuestasMalas", respuestasMalas != null ? respuestasMalas : 0);
        
        return reporte;
    }
    
    /**
     * Reporte de nuevos estudiantes por mes
     */
    public Long obtenerNuevosEstudiantes(int mes, int year) {
        return usuarioRepository.countNewStudentsByMonth(mes, year);
    }
    
    /**
     * Reporte general del sistema
     */
    public Map<String, Object> obtenerReporteGeneral() {
        Map<String, Object> reporte = new HashMap<>();
        
        long totalUsuarios = usuarioRepository.count();
        long totalEstudiantes = usuarioRepository.findByRol(RolUsuario.ESTUDIANTE).size();
        long totalInstructores = usuarioRepository.findByRol(RolUsuario.INSTRUCTOR).size();
        long totalConsultas = consultaRepository.count();
        long totalPreguntas = preguntaRepository.count();
        
        reporte.put("totalUsuarios", totalUsuarios);
        reporte.put("totalEstudiantes", totalEstudiantes);
        reporte.put("totalInstructores", totalInstructores);
        reporte.put("totalConsultas", totalConsultas);
        reporte.put("totalPreguntas", totalPreguntas);
        
        return reporte;
    }
}
