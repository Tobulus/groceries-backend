package remembrall.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import remembrall.controller.BasicController;
import remembrall.model.GroceryList;
import remembrall.model.GroceryListEntry;
import remembrall.model.User;
import remembrall.model.enums.quantity_unit.QuantityUnit;
import remembrall.model.repository.GroceryListEntryRepository;
import remembrall.model.repository.GroceryListRepository;
import remembrall.model.repository.UserRepository;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GroceryListEntryApi implements BasicController {

    @Autowired
    private GroceryListRepository groceryListRepository;

    @Autowired
    private GroceryListEntryRepository groceryListEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "/api/grocery-list/{id}/entries")
    public List<GroceryListEntry> listGroceryListEntries(@PathVariable Long id) {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(id, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));
        return groceryListEntryRepository.findByGroceryList(groceryList);
    }

    @PostMapping(value = "/api/grocery-list/{id}/entry/new")
    public Map<String, String> newGroceryListEntry(@RequestParam("name") String name,
                                                   @RequestParam("quantity") Double quantity,
                                                   @RequestParam("quantityUnit") String quantityUnitCode,
                                                   @PathVariable Long id) {
        Map<String, String> response = new HashMap<>();

        GroceryListEntry groceryListEntry = new GroceryListEntry();
        groceryListEntry.setGroceryList(groceryListRepository.getOne(id));
        groceryListEntry.setName(name);
        groceryListEntry.setQuantity(quantity);
        groceryListEntry.setQuantityUnit(QuantityUnit.from(quantityUnitCode));

        groceryListEntryRepository.save(groceryListEntry);

        response.put("result", "success");
        response.put("id", groceryListEntry.getId().toString());
        return response;
    }

    @PostMapping(value = "/api/grocery-list/{listId}/entry/{entryId}")
    public void editGroceryListEntry(@RequestParam(value = "name", required = false) String name,
                                     @RequestParam(value = "checked", required = false) Boolean checked,
                                     @RequestParam(value = "quantity", required = false) Double quantity,
                                     @RequestParam(value = "quantityUnit", required = false) String quantityUnitCode,
                                     @PathVariable Long listId,
                                     @PathVariable Long entryId) {
        GroceryListEntry groceryListEntry = fetchGroceryListEntry(listId, entryId);

        if (name != null) {
            groceryListEntry.setName(name);
        }

        if (checked != null) {
            groceryListEntry.setChecked(checked);
        }

        if (quantity != null) {
            groceryListEntry.setQuantity(quantity);
        }

        if (quantityUnitCode != null) {
            groceryListEntry.setQuantityUnit(QuantityUnit.from(quantityUnitCode));
        }

        groceryListEntryRepository.save(groceryListEntry);
    }

    @DeleteMapping(value = "/api/grocery-list/{listId}/entry/{entryId}/delete")
    public void deleteGroceryListentry(@PathVariable Long listId, @PathVariable Long entryId) {
        GroceryListEntry listEntry = fetchGroceryListEntry(listId, entryId);
        groceryListEntryRepository.delete(listEntry);
    }

    private GroceryListEntry fetchGroceryListEntry(Long groceryListId, Long entryId) {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(groceryListId, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));
        return groceryListEntryRepository.findByGroceryListAndId(groceryList, entryId);
    }
}
