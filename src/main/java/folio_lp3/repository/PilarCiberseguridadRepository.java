package folio_lp3.repository;

import folio_lp3.entity.PilarCiberseguridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository optimizado para PilarCiberseguridad.
 * Aplica cargas ansiosas controladas para evitar LazyInitializationExceptions en mapeos de DTOs.
 */
@Repository
public interface PilarCiberseguridadRepository extends JpaRepository<PilarCiberseguridad, Long> {
    
    @Query("SELECT p FROM PilarCiberseguridad p LEFT JOIN FETCH p.entorno WHERE p.nombrePilar = :nombrePilar")
    Optional<PilarCiberseguridad> findByNombrePilar(@Param("nombrePilar") String nombrePilar);
    
    @Query("SELECT p FROM PilarCiberseguridad p LEFT JOIN FETCH p.entorno WHERE p.activo = true")
    List<PilarCiberseguridad> findByActivoTrue();
    
    @Query("SELECT p FROM PilarCiberseguridad p JOIN FETCH p.entorno WHERE p.entorno.id = :entornoId")
    List<PilarCiberseguridad> findByEntornoId(@Param("entornoId") Long entornoId);
    
    @Query("SELECT p FROM PilarCiberseguridad p JOIN FETCH p.entorno WHERE p.entorno.id = :entornoId AND p.activo = true")
    List<PilarCiberseguridad> findActiveByEntorno(@Param("entornoId") Long entornoId);
}