package remembrall.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import remembrall.controller.BasicController;
import remembrall.model.GroceryList;
import remembrall.model.Invitation;
import remembrall.model.User;
import remembrall.model.repository.GroceryListEntryRepository;
import remembrall.model.repository.GroceryListRepository;
import remembrall.model.repository.InvitationRepository;
import remembrall.model.repository.UserRepository;
import remembrall.service.UserPrincipal;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GroceryListApi implements BasicController {

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
        return groceryListRepository.fetchLists(userRepository.getOne(currentUser.getUserId()));
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
