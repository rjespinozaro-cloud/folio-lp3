package folio_lp3.repository;

import folio_lp3.entity.SiemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SiemLogRepository extends JpaRepository<SiemLog, Long> {

    // Ordena por defecto los más recientes primero
    Page<SiemLog> findAllByOrderByTimestampDesc(Pageable pageable);

    /**
     * 🔥 PODA DE EXCEDENTE: mantiene solo los `maxLogs` registros más recientes.
     * Borra todo lo que tenga un timestamp más viejo que el registro que ocupa
     * la posición N (donde N = maxLogs) al ordenar todo de más nuevo a más viejo.
     * Se llama desde SiemService.saveLog() en cada inserción.
     */
    @Modifying
    @Query(value = """
        DELETE FROM siem_log
        WHERE timestamp < (
            SELECT mantener.timestamp FROM (
                SELECT timestamp FROM siem_log
                ORDER BY timestamp DESC
                LIMIT 1 OFFSET :offset
            ) AS mantener
        )
        """, nativeQuery = true)
    void deleteOldestExceeding(@Param("offset") int offset);
}