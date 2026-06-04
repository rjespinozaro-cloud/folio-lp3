package folio_lp3.repository;

import folio_lp3.entity.Herramienta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para Herramienta
 */
@Repository
public interface HerramientaRepository extends JpaRepository<Herramienta, Long> {
    Optional<Herramienta> findByNombre(String nombre);
}
