// Archivo: src/main/java/folio_lp3/repository/CertificacionesRepository.java
package folio_lp3.repository;

import folio_lp3.entity.Certificaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificacionesRepository extends JpaRepository<Certificaciones, Long> {
}