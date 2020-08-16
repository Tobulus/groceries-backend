package remembrall.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import remembrall.model.GroceryList;
import remembrall.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByGroceryLists(GroceryList list);

    @Modifying
    @Query("UPDATE User SET password = ?2, enabled = ?3 WHERE username = ?1")
    int updatePasswordAndEnabledFor(String username, String password, boolean enabled);

    boolean existsByUsername(String username);

    Long deleteByUsername(String username);

    @Modifying
    @Query("UPDATE User SET lang = ?2 WHERE username = ?1")
    void updateLangFor(String username, String lang);
}
