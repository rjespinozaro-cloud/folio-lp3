package folio_lp3.repository;

import folio_lp3.entity.Consulta;
import folio_lp3.enums.EstadoConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para Consulta
 */
@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    
    List<Consulta> findByEstudianteId(Long estudianteId);
    
    List<Consulta> findByPilarId(Long pilarId);
    
    List<Consulta> findByEstadoAndPilarId(EstadoConsulta estado, Long pilarId);
    
    List<Consulta> findByEstado(EstadoConsulta estado);
    
    @Query("SELECT c FROM Consulta c WHERE c.estado = 'PENDIENTE' AND c.ultimaActividad < :fecha")
    List<Consulta> findPendingConsultasWithInactivity(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT COUNT(c) FROM Consulta c WHERE c.estado = 'ATENDIDA' AND c.pilar.id = :pilarId AND MONTH(c.fechaCreacion) = :mes AND YEAR(c.fechaCreacion) = :year")
    Long countAttendedByPilarAndMonth(@Param("pilarId") Long pilarId, @Param("mes") int mes, @Param("year") int year);
    
    @Query("SELECT COUNT(c) FROM Consulta c WHERE c.estado = 'CANCELADA' AND c.pilar.id = :pilarId AND MONTH(c.fechaCreacion) = :mes AND YEAR(c.fechaCreacion) = :year")
    Long countCancelledByPilarAndMonth(@Param("pilarId") Long pilarId, @Param("mes") int mes, @Param("year") int year);
    
    @Query("SELECT SUM(c.cantidadTokensUsados) FROM Consulta c WHERE c.pilar.id = :pilarId AND MONTH(c.fechaCreacion) = :mes AND YEAR(c.fechaCreacion) = :year")
    Long sumTokensByPilarAndMonth(@Param("pilarId") Long pilarId, @Param("mes") int mes, @Param("year") int year);
}
