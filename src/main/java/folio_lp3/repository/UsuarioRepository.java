package folio_lp3.repository;

import folio_lp3.entity.Usuario;
import folio_lp3.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Usuario - Acceso a datos
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    List<Usuario> findByRol(RolUsuario rol);
    
    List<Usuario> findByActivoTrue();
    
    List<Usuario> findByRolAndActivoTrue(RolUsuario rol);
    
    @Query("SELECT u FROM Usuario u WHERE u.pilarAsignado.id = :pilarId AND u.rol = 'INSTRUCTOR'")
    Optional<Usuario> findInstructorByPilar(@Param("pilarId") Long pilarId);
    
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol = 'ESTUDIANTE' AND MONTH(u.createdAt) = :mes AND YEAR(u.createdAt) = :year")
    Long countNewStudentsByMonth(@Param("mes") int mes, @Param("year") int year);
}
