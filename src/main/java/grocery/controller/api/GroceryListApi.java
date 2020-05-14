package grocery.controller.api;

import grocery.model.GroceryList;
import grocery.model.Invitation;
import grocery.model.User;
import grocery.model.repository.GroceryListEntryRepository;
import grocery.model.repository.GroceryListRepository;
import grocery.model.repository.InvitationRepository;
import grocery.model.repository.UserRepository;
import grocery.service.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GroceryListApi implements BasicApiController {

    @Autowired
    private GroceryListRepository groceryListRepository;

    @Autowired
    private GroceryListEntryRepository groceryListEntryRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "/api/grocery-lists")
    public List<GroceryList> groceryLists() {
        UserPrincipal currentUser = getUserPrincipalOrThrow();
        List<GroceryList> groceryLists =
                groceryListRepository.findByUsers(userRepository.getOne(currentUser.getUserId()));
        // TODO: caching
        groceryLists.forEach(list -> {
            list.setNumberOfEntries(groceryListEntryRepository.countByGroceryList(list));
            list.setNumberOfCheckedEntries(groceryListEntryRepository.countByGroceryListAndChecked(list, true));
        });
        return groceryLists;
    }

    @PostMapping(value = "/api/grocery-list/new")
    public Map<String, String> newGroceryList(@RequestParam("name") String name) {
        UserPrincipal currentUser = getUserPrincipalOrThrow();
        Map<String, String> response = new HashMap<>();

        GroceryList groceryList = new GroceryList();
        groceryList.setName(name);
        groceryList.getUsers().add(userRepository.getOne(currentUser.getUserId()));

        groceryListRepository.save(groceryList);
        response.put("result", "success");
        response.put("id", groceryList.getId().toString());
        return response;
    }

    @PostMapping(value = "/api/grocery-list/{id}/invite")
    public Map<String, String> share(@PathVariable Long id, @RequestParam String email) {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(id, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));
        User receiver = userRepository.findByUsername(email).orElseThrow(
                () -> new IllegalArgumentException("The invited user doesn't exist."));

        Map<String, String> result = new HashMap<>();

        Invitation invitation = new Invitation();
        invitation.setSender(currentUser);
        invitation.setReceiver(receiver);
        invitation.setGroceryList(groceryList);
        invitationRepository.save(invitation);
        result.put("result", "success");

        return result;
    }

    @DeleteMapping(value = "/api/grocery-list/{id}/delete")
    public Map<String, String> deleteGroceryList(@PathVariable Long id) {
        Map<String, String> result = new HashMap<>();

        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(id, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));
        groceryListRepository.delete(groceryList);

        result.put("result", "success");
        return result;
    }
}
