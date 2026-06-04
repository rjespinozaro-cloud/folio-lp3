package folio_lp3.repository;

import folio_lp3.entity.DetalleComandoPilar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para DetalleComandoPilar
 */
@Repository
public interface DetalleComandoPilarRepository extends JpaRepository<DetalleComandoPilar, Long> {
    
    List<DetalleComandoPilar> findByPilarId(Long pilarId);
    
    List<DetalleComandoPilar> findByHerramientaId(Long herramientaId);
    
    @Query("SELECT d FROM DetalleComandoPilar d WHERE d.pilar.id = :pilarId AND d.activo = true")
    List<DetalleComandoPilar> findActiveByPilar(@Param("pilarId") Long pilarId);
    
    @Query("SELECT d FROM DetalleComandoPilar d WHERE d.pilar.id = :pilarId AND d.herramienta.id = :herramientaId AND d.tipoComando = :tipoComando")
    List<DetalleComandoPilar> findByPilarAndHerramientaAndTipo(@Param("pilarId") Long pilarId, @Param("herramientaId") Long herramientaId, @Param("tipoComando") String tipoComando);
}
