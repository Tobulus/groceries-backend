package grocery.controller.api;

import grocery.model.GroceryList;
import grocery.model.GroceryListEntry;
import grocery.model.User;
import grocery.model.repository.GroceryListEntryRepository;
import grocery.model.repository.GroceryListRepository;
import grocery.model.repository.UserRepository;
import grocery.service.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GroceryListEntryApi implements BasicApiController {

    @Autowired
    private GroceryListRepository groceryListRepository;

    @Autowired
    private GroceryListEntryRepository groceryListEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "/api/grocery-list/{id}/entries")
    public List<GroceryListEntry> listGroceryListEntries(@PathVariable Long id, Model model) {
        UserPrincipal principal = getUserPrincipalOrThrow();
        User currentUser = userRepository.findById(principal.getUserId()).orElseThrow(
                () -> new InvalidParameterException("Cannot find user with id: " + principal.getUserId()));
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(id, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));
        return groceryListEntryRepository.findByGroceryList(groceryList);
    }

    @PostMapping(value = "/api/grocery-list/{id}/entry/new")
    public Map<String, String> newGroceryListEntry(@RequestParam("name") String name,
                                                   @PathVariable Long id) throws IOException {
        Map<String, String> response = new HashMap<>();

        GroceryListEntry groceryListEntry = new GroceryListEntry();
        groceryListEntry.setGroceryList(groceryListRepository.findById(id).orElseThrow(
                () -> new InvalidParameterException("Cannot find list with id: " + id)));
        groceryListEntry.setName(name);

        groceryListEntryRepository.save(groceryListEntry);

        response.put("result", "success");
        response.put("id", groceryListEntry.getId().toString());
        return response;
    }

    @PostMapping(value = "/api/grocery-list/{listId}/entry/{entryId}")
    public void editGroceryListEntry(@RequestParam(value = "name", required = false) String name,
                                     @RequestParam(value = "checked", required = false) Boolean checked,
                                     @PathVariable Long listId,
                                     @PathVariable Long entryId) {
        GroceryListEntry groceryListEntry = fetchGroceryListEntry(listId, entryId);

        if (name != null) {
            groceryListEntry.setName(name);
        }

        if (checked != null) {
            groceryListEntry.setChecked(checked);
        }

        groceryListEntryRepository.save(groceryListEntry);
    }

    @DeleteMapping(value = "/api/grocery-list/{listId}/entry/{entryId}/delete")
    public void deleteGroceryListentry(@PathVariable Long listId, @PathVariable Long entryId) {
        GroceryListEntry listEntry = fetchGroceryListEntry(listId, entryId);
        groceryListEntryRepository.delete(listEntry);
    }

    private GroceryListEntry fetchGroceryListEntry(Long groceryListId, Long entryId) {
        UserPrincipal principal = getUserPrincipalOrThrow();
        User currentUser = userRepository.findById(principal.getUserId()).orElseThrow(
                () -> new InvalidParameterException("Cannot find user with id: " + principal.getUserId()));
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(groceryListId, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));
        return groceryListEntryRepository.findByGroceryListAndId(groceryList, entryId);
    }
}
