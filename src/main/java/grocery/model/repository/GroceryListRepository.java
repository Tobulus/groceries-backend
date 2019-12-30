package grocery.model.repository;

import grocery.model.GroceryList;
import grocery.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface GroceryListRepository extends CrudRepository<GroceryList, Long> {
    List<GroceryList> findByUser(User user);

    Optional<GroceryList> findByIdAndUser(Long id, User user);
}
