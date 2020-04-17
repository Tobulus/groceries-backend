package grocery.controller.api;

import grocery.model.GroceryList;
import grocery.model.repository.GroceryListRepository;
import grocery.model.repository.UserRepository;
import grocery.service.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GroceryListJsonApi {

    @Autowired
    private GroceryListRepository groceryListRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "/api/grocery-lists")
    public List<GroceryList> groceryLists() {
        UserPrincipal currentUser = getUserPrincipalOrThrow();
        return groceryListRepository.findByUsers(userRepository.getOne(currentUser.getUserId()));
    }

    @PostMapping(value = "/api/grocery-list/new")
    public Map<String, String> newGroceryList(@RequestParam("name") String name) throws IOException {
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

    private UserPrincipal getUserPrincipalOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Please log in");
        }

        return (UserPrincipal) auth.getPrincipal();
    }
}
