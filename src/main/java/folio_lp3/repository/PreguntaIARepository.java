package folio_lp3.repository;

import folio_lp3.entity.PreguntaIA;
import folio_lp3.enums.CalificacionIA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para PreguntaIA
 */
@Repository
public interface PreguntaIARepository extends JpaRepository<PreguntaIA, Long> {
    
    List<PreguntaIA> findByConsultaId(Long consultaId);
    
    @Query("SELECT p FROM PreguntaIA p WHERE p.consulta.pilar.id = :pilarId AND p.calificacion = folio_lp3.enums.CalificacionIA.MALA")
    List<PreguntaIA> findBadRatingsByPilar(@Param("pilarId") Long pilarId);
    
    @Query("SELECT COUNT(p) FROM PreguntaIA p WHERE p.consulta.pilar.id = :pilarId AND p.calificacion = folio_lp3.enums.CalificacionIA.MALA AND MONTH(p.fechaHora) = :mes AND YEAR(p.fechaHora) = :year")
    Long countBadRatingsByPilarAndMonth(@Param("pilarId") Long pilarId, @Param("mes") int mes, @Param("year") int year);
    
    @Query("SELECT SUM(p.tokensConsumidos) FROM PreguntaIA p WHERE p.consulta.id = :consultaId")
    Integer sumTokensByConsulta(@Param("consultaId") Long consultaId);
}
