package folio_lp3.service;

import folio_lp3.entity.SiemLog;
import folio_lp3.repository.SiemLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 🚀 Importado para logs de consola
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled; // 🚀 Importado para el planificador
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j // 🔥 Habilita la telemetría 'log.info' en tu consola de Parrot Linux / Windows
public class SiemService {

    private final SiemLogRepository siemLogRepository;

    public void saveLog(SiemLog siemLog) {
        siemLogRepository.save(siemLog);
    }

    public Page<SiemLog> getLatestLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return siemLogRepository.findAllByOrderByTimestampDesc(pageable);
    }

    /**
     * Método de purga manual invocado por el Administrador desde el botón del Dashboard.
     */
    @Transactional
    public void purgarTodosLosLogs() {
        siemLogRepository.deleteAll();
    }

    /**
     * 🔥 ROBOT DE ROTACIÓN AUTOMÁTICA (Borrado Giratorio)
     * Se activa en segundo plano cada 30 minutos (1800000 ms).
     * Si detecta acumulación excesiva en MariaDB, limpia la casa por ti.
     */
    @Scheduled(fixedRate = 1800000) 
    @Transactional
    public void rotarLogsAutomaticamente() {
        log.info("🛡️ [CRON SIEM] Verificando estado y volumen de almacenamiento en MariaDB...");
        
        long totalLogs = siemLogRepository.count();
        
        // Si la base de datos supera los 200 logs, se realiza una poda de seguridad automatizada
        if (totalLogs > 200) {
            log.warn("⚠️ Alerta de volumen: Se detectaron {} registros acumulados. Ejecutando poda preventiva...", totalLogs);
            
            siemLogRepository.deleteAll(); // Reinicia la tabla físicamente
            
            log.info("✅ Poda completada con éxito. Base de datos optimizada y espacio liberado.");
        } else {
            log.info("ℹ️ Estado de almacenamiento saludable ({} de un máximo seguro de 200 logs). No se requiere acción.", totalLogs);
        }
    }
}