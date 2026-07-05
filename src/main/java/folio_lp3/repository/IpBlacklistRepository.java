package folio_lp3.repository;



import folio_lp3.entity.IpBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IpBlacklistRepository extends JpaRepository<IpBlacklist, Long> {
    Optional<IpBlacklist> findByIpBloqueada(String ipBloqueada);
}