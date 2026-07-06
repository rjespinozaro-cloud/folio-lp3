package folio_lp3.service;

import folio_lp3.entity.SiemLog;
import folio_lp3.repository.SiemLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiemService {

    private final SiemLogRepository siemLogRepository;

    // 🔥 TOPE DURO: la tabla nunca supera este número de filas
    private static final int MAX_LOGS = 50;

    /**
     * 🔥 GUARDADO CON TOPE DURO DE 50
     * Cada vez que entra un log nuevo (incluido el propio polling del dashboard,
     * ya que el interceptor ya no lo excluye), se inserta y luego se poda
     * cualquier excedente por encima de MAX_LOGS. Efecto resultante: la tabla
     * siempre se mueve tipo escalera, nunca crece sin control.
     */
    @Transactional
    public void saveLog(SiemLog siemLog) {
        siemLogRepository.save(siemLog);

        long total = siemLogRepository.count();
        if (total > MAX_LOGS) {
            siemLogRepository.deleteOldestExceeding(MAX_LOGS);
        }
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

    // ℹ️ Ya no se necesita el @Scheduled de rotación cada 30 min: con el tope
    // de 50 aplicado en cada saveLog(), la poda ocurre en tiempo real y no
    // por temporizador. Si más adelante quieres una poda de respaldo (por si
    // algún día insertas logs sin pasar por este service), se puede reintroducir.
}