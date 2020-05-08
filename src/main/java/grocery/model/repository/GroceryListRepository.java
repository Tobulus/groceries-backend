package grocery.model.repository;

import grocery.model.GroceryList;
import grocery.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroceryListRepository extends JpaRepository<GroceryList, Long> {
    List<GroceryList> findByUsers(User user);

    Optional<GroceryList> findByIdAndUsers(Long id, User user);

    @EntityGraph(value = "GroceryList.Users")
    Optional<GroceryList> findEagerUsersById(Long id);
}
