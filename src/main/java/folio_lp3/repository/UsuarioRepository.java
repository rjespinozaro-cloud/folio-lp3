package folio_lp3.repository;

import folio_lp3.entity.Usuario;
import folio_lp3.enums.RolUsuario;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @Override
    @EntityGraph(attributePaths = {"pilarAsignado"})
    List<Usuario> findAll();



    @EntityGraph(attributePaths = {"pilarAsignado"})
    Optional<Usuario> findByEmail(String email);

    @EntityGraph(attributePaths = {"pilarAsignado"})
    List<Usuario> findByRol(RolUsuario rol);

    @EntityGraph(attributePaths = {"pilarAsignado"})
    List<Usuario> findByActivoTrue();

    List<Usuario> findByRolAndActivoTrue(RolUsuario rol);

    @Query("SELECT u FROM Usuario u WHERE u.pilarAsignado.id = :pilarId AND u.rol = folio_lp3.enums.RolUsuario.INSTRUCTOR")
    Optional<Usuario> findInstructorByPilar(@Param("pilarId") Long pilarId);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol = folio_lp3.enums.RolUsuario.ESTUDIANTE AND MONTH(u.createdAt) = :mes AND YEAR(u.createdAt) = :year")
    Long countNewStudentsByMonth(@Param("mes") int mes, @Param("year") int year);
}
