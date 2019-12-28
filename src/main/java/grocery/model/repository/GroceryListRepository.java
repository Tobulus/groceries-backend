package grocery.model.repository;

import grocery.model.GroceryList;
import org.springframework.data.repository.CrudRepository;

public interface GroceryListRepository extends CrudRepository<GroceryList, Long> {
}
