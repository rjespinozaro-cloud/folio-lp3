package folio_lp3.repository;

import folio_lp3.entity.SiemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiemLogRepository extends JpaRepository<SiemLog, Long> {
    // Ordena por defecto los más recientes primero
    Page<SiemLog> findAllByOrderByTimestampDesc(Pageable pageable);
}