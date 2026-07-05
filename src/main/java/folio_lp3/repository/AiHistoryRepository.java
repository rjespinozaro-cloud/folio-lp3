package folio_lp3.repository;

import folio_lp3.entity.AiHistoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AiHistoryRepository extends JpaRepository<AiHistoryLog, Long> {
    // Recuperar el historial de auditoría ordenado de forma cronológica inversa (lo más nuevo primero)
    List<AiHistoryLog> findAllByOrderByTimestampDesc();
}