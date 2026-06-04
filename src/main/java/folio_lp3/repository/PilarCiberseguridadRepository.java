package folio_lp3.repository;

import folio_lp3.entity.PilarCiberseguridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para PilarCiberseguridad
 */
@Repository
public interface PilarCiberseguridadRepository extends JpaRepository<PilarCiberseguridad, Long> {
    
    Optional<PilarCiberseguridad> findByNombrePilar(String nombrePilar);
    
    List<PilarCiberseguridad> findByActivoTrue();
    
    List<PilarCiberseguridad> findByEntornoId(Long entornoId);
    
    @Query("SELECT p FROM PilarCiberseguridad p WHERE p.entorno.id = :entornoId AND p.activo = true")
    List<PilarCiberseguridad> findActiveByEntorno(@Param("entornoId") Long entornoId);
}
