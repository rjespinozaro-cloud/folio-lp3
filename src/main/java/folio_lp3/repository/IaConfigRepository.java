// Archivo: src/main/java/folio_lp3/repository/IaConfigRepository.java
package folio_lp3.repository;

import folio_lp3.entity.IaConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IaConfigRepository extends JpaRepository<IaConfig, Long> {
    // Retorna la configuración activa actual (usualmente el ID 1)
}