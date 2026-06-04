package folio_lp3.service;

import folio_lp3.entity.Consulta;
import folio_lp3.enums.EstadoConsulta;
import folio_lp3.repository.ConsultaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de Tareas Automatizadas (Scheduler)
 * Ejecuta verificaciones periódicas en el sistema
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {
    
    private final ConsultaRepository consultaRepository;
    
    /**
     * Ejecuta cada 10 minutos (600000 ms)
     * Cancela automáticamente consultas PENDIENTES con más de 2 horas de inactividad
     */
    @Scheduled(fixedDelay = 600000)
    @Transactional
    public void cancelarConsultasInactivas() {
        log.info("Iniciando verificación de consultas inactivas...");
        
        LocalDateTime hace2Horas = LocalDateTime.now().minusHours(2);
        List<Consulta> consultasInactivas = consultaRepository.findPendingConsultasWithInactivity(hace2Horas);
        
        for (Consulta consulta : consultasInactivas) {
            consulta.setEstado(EstadoConsulta.CANCELADA);
            consulta.setMotivoCancelacion("Cancelada automáticamente por inactividad (>2 horas)");
            consulta.setFechaCierre(LocalDateTime.now());
            consultaRepository.save(consulta);
            log.info("Consulta {} cancelada automáticamente", consulta.getId());
        }
        
        log.info("Verificación completada. {} consultas canceladas", consultasInactivas.size());
    }
    
    /**
     * Ejecuta diariamente a las 02:00 AM
     * Genera alertas de calidad (respuestas malas)
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void verificarAlertsCalidad() {
        log.info("Iniciando verificación de alertas de calidad...");
        // Lógica para generar reportes de calidad
        log.info("Verificación de alertas completada");
    }
}
