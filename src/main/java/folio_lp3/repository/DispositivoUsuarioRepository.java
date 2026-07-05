package folio_lp3.repository;


import folio_lp3.entity.DispositivoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DispositivoUsuarioRepository extends JpaRepository<DispositivoUsuario, Long> {
    List<DispositivoUsuario> findByActivaTrue();
    Optional<DispositivoUsuario> findByUsuarioEmailAndIpOrigenAndDispositivo(String usuarioEmail, String ipOrigen, String dispositivo);
}