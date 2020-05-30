package rmbr.model.repository;

import org.springframework.data.repository.CrudRepository;
import rmbr.model.GroceryList;
import rmbr.model.GroceryListEntry;

import java.util.List;

public interface GroceryListEntryRepository extends CrudRepository<GroceryListEntry, Long> {
    List<GroceryListEntry> findByGroceryList(GroceryList groceryList);

    GroceryListEntry findByGroceryListAndId(GroceryList groceryList, Long id);

    Long countByGroceryList(GroceryList groceryList);

    Long countByGroceryListAndChecked(GroceryList groceryList, Boolean checked);
}
