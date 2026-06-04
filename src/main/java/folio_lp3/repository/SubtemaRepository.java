package folio_lp3.repository;

import folio_lp3.entity.Subtema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para Subtema
 */
@Repository
public interface SubtemaRepository extends JpaRepository<Subtema, Long> {
    Optional<Subtema> findByCodigo(String codigo);
}
