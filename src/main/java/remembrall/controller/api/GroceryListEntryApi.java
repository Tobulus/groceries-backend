package remembrall.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import remembrall.controller.BasicController;
import remembrall.model.GroceryList;
import remembrall.model.GroceryListEntry;
import remembrall.model.RedisKeys;
import remembrall.model.User;
import remembrall.model.enums.quantity_unit.QuantityUnit;
import remembrall.model.repository.GroceryListEntryRepository;
import remembrall.model.repository.GroceryListRepository;
import remembrall.model.repository.UserRepository;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class GroceryListEntryApi implements BasicController {

    Logger logger = LoggerFactory.getLogger(GroceryListEntryApi.class);

    @Autowired
    private GroceryListRepository groceryListRepository;

    @Autowired
    private GroceryListEntryRepository groceryListEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redis;

    @GetMapping(value = "/api/grocery-list/{id}/entries")
    public List<GroceryListEntry> listGroceryListEntries(@PathVariable Long id) {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(id, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));

        String timestamp =
                (String) redis.opsForHash().get(RedisKeys.LAST_SEEN, String.format("%s-%s", id, currentUser.getId()));
        long lastSeen = timestamp == null ? System.currentTimeMillis() : Long.parseLong(timestamp);
        List<GroceryListEntry> lists = groceryListEntryRepository.findByGroceryList(groceryList);
        lists.forEach(list -> list.setUnseen(list.getAudit().getCreatedDate() > lastSeen));

        // remove all queued pushes for the current grocerylist
        redis.opsForZSet().remove(RedisKeys.PUSH_NEW_ENTRY, String.format("%s-%s", id, currentUser.getId()));
        // save 'last seen' timestamp
        redis.opsForHash().put(RedisKeys.LAST_SEEN, String.format("%s-%s", id, currentUser.getId()),
                               String.valueOf(System.currentTimeMillis()));

        return lists;
    }

    @PostMapping(value = "/api/grocery-list/{id}/entry/new")
    public Map<String, String> newGroceryListEntry(@RequestParam("name") String name,
                                                   @RequestParam(value = "quantity", required = false) Double quantity,
                                                   @RequestParam(value = "quantityUnit", required = false) String quantityUnitCode,
                                                   @PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        Long currentUser = getUserPrincipalOrThrow().getUserId();

        GroceryListEntry groceryListEntry = new GroceryListEntry();
        groceryListEntry.setGroceryList(groceryListRepository.getOne(id));
        groceryListEntry.setName(name);
        groceryListEntry.setQuantity(quantity);
        groceryListEntry.setQuantityUnit(QuantityUnit.from(quantityUnitCode));

        groceryListEntryRepository.save(groceryListEntry);

        userRepository.findByGroceryLists(groceryListRepository.getOne(id)).forEach(
                user -> {
                    if (!Objects.equals(currentUser, user.getId())) {
                        redis.opsForZSet()
                             .add(RedisKeys.PUSH_NEW_ENTRY, String.format("%s-%s", id, user.getId()),
                                  System.currentTimeMillis());
                    }
                });

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

        groceryListEntry.setQuantity(quantity);
        groceryListEntry.setQuantityUnit(QuantityUnit.from(quantityUnitCode));

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
