package folio_lp3.repository;

import folio_lp3.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para Auditoria
 */
@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findByUsuarioId(Long usuarioId);
    List<Auditoria> findByTablaAfectada(String tablaAfectada);
}
