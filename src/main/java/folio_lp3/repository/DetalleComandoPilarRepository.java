package folio_lp3.repository;

import folio_lp3.entity.DetalleComandoPilar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository de alto rendimiento para comandos y mitigaciones.
 * Utiliza JOIN FETCH total para neutralizar el problema de consultas N+1 en las vistas de evidencias.
 */
@Repository
public interface DetalleComandoPilarRepository extends JpaRepository<DetalleComandoPilar, Long> {
    
    @Query("SELECT d FROM DetalleComandoPilar d JOIN FETCH d.pilar JOIN FETCH d.herramienta LEFT JOIN FETCH d.subtema")
    List<DetalleComandoPilar> findAll();

    @Query("SELECT d FROM DetalleComandoPilar d JOIN FETCH d.pilar JOIN FETCH d.herramienta LEFT JOIN FETCH d.subtema WHERE d.pilar.id = :pilarId")
    List<DetalleComandoPilar> findByPilarId(@Param("pilarId") Long pilarId);
    
    @Query("SELECT d FROM DetalleComandoPilar d JOIN FETCH d.pilar JOIN FETCH d.herramienta LEFT JOIN FETCH d.subtema WHERE d.herramienta.id = :herramientaId")
    List<DetalleComandoPilar> findByHerramientaId(@Param("herramientaId") Long herramientaId);
    
    @Query("SELECT d FROM DetalleComandoPilar d JOIN FETCH d.pilar JOIN FETCH d.herramienta LEFT JOIN FETCH d.subtema WHERE d.pilar.id = :pilarId AND d.activo = true")
    List<DetalleComandoPilar> findActiveByPilar(@Param("pilarId") Long pilarId);
    
    @Query("SELECT DISTINCT d.tipoComando FROM DetalleComandoPilar d WHERE d.tipoComando IS NOT NULL")
    List<String> findDistinctTipoComando();

    @Query("SELECT d FROM DetalleComandoPilar d JOIN FETCH d.pilar JOIN FETCH d.herramienta LEFT JOIN FETCH d.subtema WHERE d.pilar.id = :pilarId AND d.herramienta.id = :herramientaId AND d.tipoComando = :tipoComando")
    List<DetalleComandoPilar> findByPilarAndHerramientaAndTipo(@Param("pilarId") Long pilarId, @Param("herramientaId") Long herramientaId, @Param("tipoComando") String tipoComando);

    // Consulta Core para el Cyber Assistant: Busca en la sintaxis, mitigaciones y vulnerabilidades
    @Query("SELECT d FROM DetalleComandoPilar d JOIN FETCH d.pilar JOIN FETCH d.herramienta LEFT JOIN FETCH d.subtema " +
           "WHERE LOWER(d.sintaxis) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(d.vulnerabilidadAsociada) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(d.mitigacion) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<DetalleComandoPilar> searchComandosForIA(@Param("query") String query);
}