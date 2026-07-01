package folio_lp3.repository;

import folio_lp3.entity.Herramienta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository optimizado para la gestión de Herramientas del Portafolio Cyber.
 */
@Repository
public interface HerramientaRepository extends JpaRepository<Herramienta, Long> {
    
    Optional<Herramienta> findByNombre(String nombre);

    // Consulta segura e indexada para las búsquedas contextuales de la IA
    @Query("SELECT h FROM Herramienta h WHERE LOWER(h.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(h.descripcion) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Herramienta> searchByKeyword(@Param("keyword") String keyword);
}