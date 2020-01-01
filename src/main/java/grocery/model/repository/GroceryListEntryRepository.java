package grocery.model.repository;

import grocery.model.GroceryList;
import grocery.model.GroceryListEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroceryListEntryRepository extends CrudRepository<GroceryListEntry, Long> {
    List<GroceryListEntry> findByGroceryList(GroceryList groceryList);

    GroceryListEntry findByGroceryListAndId(GroceryList groceryList, Long id);
}
