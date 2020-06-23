package remembrall.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import remembrall.model.GroceryList;
import remembrall.model.GroceryListEntry;

import java.util.List;

public interface GroceryListEntryRepository extends JpaRepository<GroceryListEntry, Long> {
    List<GroceryListEntry> findByGroceryList(GroceryList groceryList);

    GroceryListEntry findByGroceryListAndId(GroceryList groceryList, Long id);

    Long countByGroceryList(GroceryList groceryList);

    Long countByGroceryListAndChecked(GroceryList groceryList, Boolean checked);
}
