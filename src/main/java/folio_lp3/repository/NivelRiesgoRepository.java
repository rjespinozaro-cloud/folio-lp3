package folio_lp3.repository;

import folio_lp3.entity.NivelRiesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para NivelRiesgo
 */
@Repository
public interface NivelRiesgoRepository extends JpaRepository<NivelRiesgo, Long> {
    Optional<NivelRiesgo> findByCodigo(String codigo);
}
