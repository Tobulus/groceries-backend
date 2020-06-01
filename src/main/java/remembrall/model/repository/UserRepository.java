package remembrall.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import remembrall.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
