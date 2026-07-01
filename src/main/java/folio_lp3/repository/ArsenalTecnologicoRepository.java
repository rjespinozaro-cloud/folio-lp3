// Archivo: src/main/java/folio_lp3/repository/ArsenalTecnologicoRepository.java
package folio_lp3.repository;

import folio_lp3.entity.ArsenalTecnologico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArsenalTecnologicoRepository extends JpaRepository<ArsenalTecnologico, Long> {
    boolean existsByTecnologia(String tecnologia);
}