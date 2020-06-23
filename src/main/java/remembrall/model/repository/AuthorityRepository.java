package remembrall.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import remembrall.model.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
