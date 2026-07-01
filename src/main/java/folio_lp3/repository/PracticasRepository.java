package folio_lp3.repository;

import folio_lp3.entity.Practicas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticasRepository extends JpaRepository<Practicas, Long> {
}