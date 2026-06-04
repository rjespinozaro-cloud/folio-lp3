package folio_lp3.service;

import folio_lp3.dto.PreguntaIADTO;
import folio_lp3.entity.Consulta;
import folio_lp3.entity.PreguntaIA;
import folio_lp3.enums.CalificacionIA;
import folio_lp3.repository.ConsultaRepository;
import folio_lp3.repository.PreguntaIARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para PreguntaIA - Lógica del chat con IA
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PreguntaIAService {
    
    private final PreguntaIARepository preguntaRepository;
    private final ConsultaRepository consultaRepository;
    private final ConsultaService consultaService;
    
    // CREATE
    public PreguntaIADTO crearPregunta(Long consultaId, String preguntaEstudiante, String respuestaIA, Integer tokensConsumidos) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
        
        PreguntaIA pregunta = PreguntaIA.builder()
                .consulta(consulta)
                .preguntaEstudiante(preguntaEstudiante)
                .respuestaIA(respuestaIA)
                .tokensConsumidos(tokensConsumidos != null ? tokensConsumidos : 0)
                .build();
        
        PreguntaIA guardada = preguntaRepository.save(pregunta);
        
        // Actualizar tokens en consulta
        consultaService.agregarTokens(consultaId, tokensConsumidos != null ? tokensConsumidos : 0);
        
        return convertirADTO(guardada);
    }
    
    // READ
    public PreguntaIADTO obtenerPorId(Long id) {
        return preguntaRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new RuntimeException("Pregunta no encontrada"));
    }
    
    public List<PreguntaIADTO> listarPorConsulta(Long consultaId) {
        return preguntaRepository.findByConsultaId(consultaId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public List<PreguntaIADTO> listarTodas() {
        return preguntaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // UPDATE
    public PreguntaIADTO calificar(Long id, CalificacionIA calificacion) {
        PreguntaIA pregunta = preguntaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pregunta no encontrada"));
        
        pregunta.setCalificacion(calificacion);
        PreguntaIA actualizada = preguntaRepository.save(pregunta);
        return convertirADTO(actualizada);
    }
    
    // REPORTES
    public Long obtenerCalificacionesMalas(Long pilarId, int mes, int year) {
        return preguntaRepository.countBadRatingsByPilarAndMonth(pilarId, mes, year);
    }
    
    public List<PreguntaIADTO> obtenerRespuestasMalas(Long pilarId) {
        return preguntaRepository.findBadRatingsByPilar(pilarId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    private PreguntaIADTO convertirADTO(PreguntaIA pregunta) {
        return PreguntaIADTO.builder()
                .id(pregunta.getId())
                .consultaId(pregunta.getConsulta().getId())
                .preguntaEstudiante(pregunta.getPreguntaEstudiante())
                .respuestaIA(pregunta.getRespuestaIA())
                .tokensConsumidos(pregunta.getTokensConsumidos())
                .calificacion(pregunta.getCalificacion())
                .fechaHora(pregunta.getFechaHora())
                .build();
    }
}