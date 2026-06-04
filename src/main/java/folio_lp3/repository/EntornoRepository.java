package folio_lp3.repository;

import folio_lp3.entity.Entorno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para Entorno
 */
@Repository
public interface EntornoRepository extends JpaRepository<Entorno, Long> {
    Optional<Entorno> findByNombre(String nombre);
}
