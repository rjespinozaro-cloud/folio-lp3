package folio_lp3.repository;

import folio_lp3.entity.Consulta;
import folio_lp3.enums.EstadoConsulta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    
    @Override
    @EntityGraph(attributePaths = {"estudiante", "pilar"})
    List<Consulta> findAll();

    @EntityGraph(attributePaths = {"estudiante", "pilar"})
    List<Consulta> findByEstudianteId(@Param("estudianteId") Long estudianteId);
    
    @EntityGraph(attributePaths = {"estudiante", "pilar"})
    List<Consulta> findByPilarId(@Param("pilarId") Long pilarId);
    
    @EntityGraph(attributePaths = {"estudiante", "pilar"})
    List<Consulta> findByEstadoAndPilarId(@Param("estado") EstadoConsulta estado, @Param("pilarId") Long pilarId);
    
    @EntityGraph(attributePaths = {"estudiante", "pilar"})
    List<Consulta> findByEstado(@Param("estado") EstadoConsulta estado);
    
    // Schedulers y Reportes (No necesitan FETCH porque no se exponen al DTO directamente, pero es buena práctica)
    @Query("SELECT c FROM Consulta c JOIN FETCH c.estudiante JOIN FETCH c.pilar WHERE c.estado = 'PENDIENTE' AND c.ultimaActividad < :fecha")
    List<Consulta> findPendingConsultasWithInactivity(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT COUNT(c) FROM Consulta c WHERE c.estado = 'ATENDIDA' AND c.pilar.id = :pilarId AND MONTH(c.fechaCreacion) = :mes AND YEAR(c.fechaCreacion) = :year")
    Long countAttendedByPilarAndMonth(@Param("pilarId") Long pilarId, @Param("mes") int mes, @Param("year") int year);
    
    @Query("SELECT COUNT(c) FROM Consulta c WHERE c.estado = 'CANCELADA' AND c.pilar.id = :pilarId AND MONTH(c.fechaCreacion) = :mes AND YEAR(c.fechaCreacion) = :year")
    Long countCancelledByPilarAndMonth(@Param("pilarId") Long pilarId, @Param("mes") int mes, @Param("year") int year);
    
    @Query("SELECT SUM(c.cantidadTokensUsados) FROM Consulta c WHERE c.pilar.id = :pilarId AND MONTH(c.fechaCreacion) = :mes AND YEAR(c.fechaCreacion) = :year")
    Long sumTokensByPilarAndMonth(@Param("pilarId") Long pilarId, @Param("mes") int mes, @Param("year") int year);
}