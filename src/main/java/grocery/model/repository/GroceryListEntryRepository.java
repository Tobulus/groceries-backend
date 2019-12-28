package grocery.model.repository;

import grocery.model.GroceryListEntry;
import org.springframework.data.repository.CrudRepository;

public interface GroceryListEntryRepository extends CrudRepository<GroceryListEntry, Long> {
}
