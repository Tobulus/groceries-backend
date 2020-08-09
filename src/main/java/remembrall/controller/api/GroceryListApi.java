package remembrall.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import remembrall.config.user.UserPrincipal;
import remembrall.controller.BasicController;
import remembrall.model.GroceryList;
import remembrall.model.Invitation;
import remembrall.model.User;
import remembrall.model.repository.GroceryListEntryRepository;
import remembrall.model.repository.GroceryListRepository;
import remembrall.model.repository.InvitationRepository;
import remembrall.model.repository.UserRepository;
import remembrall.service.PushService;

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

    @Autowired
    private PushService pushService;

    @GetMapping(value = "/api/grocery-lists")
    public List<GroceryList> groceryLists() {
        UserPrincipal currentUser = getUserPrincipalOrThrow();
        return groceryListRepository.fetchLists(userRepository.getOne(currentUser.getUserId()), false);
    }

    @GetMapping(value = "/api/archived-grocery-lists")
    public List<GroceryList> archivedGroceryLists() {
        UserPrincipal currentUser = getUserPrincipalOrThrow();
        return groceryListRepository.fetchLists(userRepository.getOne(currentUser.getUserId()), true);
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

    @PostMapping(value = "/api/grocery-list/{id}")
    public void editGroceryList(@PathVariable Long id, @RequestParam String name, @RequestParam Boolean archived) {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(id, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));

        groceryList.setName(name);
        if (archived != null) {
            groceryList.setArchived(archived);
        }

        groceryListRepository.save(groceryList);
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

        pushService.sendInvitationPush(receiver.getId(), currentUser.getId());

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
