package grocery.controller;

import grocery.model.GroceryList;
import grocery.model.User;
import grocery.model.repository.GroceryListRepository;
import grocery.model.repository.UserRepository;
import grocery.service.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.Principal;

@Controller
public class GroceryController {

    @Autowired
    private GroceryListRepository groceryListRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "/grocery-lists")
    public ModelAndView groceryLists(Principal principal) {
        UserPrincipal currentUser = getUserPrincipalOrThrow();
        return new ModelAndView("grocery-lists", "groceryLists",
                                groceryListRepository.findByUser(userRepository.getOne(currentUser.getUserId())));
    }

    @GetMapping(value = "/grocery-list/{id}")
    public ModelAndView editGroceryList(@PathVariable Long id) {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUser(id, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));
        return new ModelAndView("grocery-list", "groceryList", groceryList);
    }

    @PostMapping(value = "/grocery-list/{id}")
    public void editGroceryList(@PathVariable Long id, @RequestParam String name) {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUser(id, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));

        groceryList.setName(name);

        groceryListRepository.save(groceryList);
    }

    @GetMapping(value = "/grocery-list/new")
    public ModelAndView newGroceryList() {
        return new ModelAndView("grocery-list", "groceryList", new GroceryList());
    }

    @PostMapping(value = "/grocery-list/new")
    public void newGroceryList(@RequestParam("name") String name, HttpServletResponse response) throws IOException {
        UserPrincipal currentUser = getUserPrincipalOrThrow();

        GroceryList groceryList = new GroceryList();
        groceryList.setName(name);
        groceryList.setUser(userRepository.getOne(currentUser.getUserId()));

        groceryListRepository.save(groceryList);

        response.sendRedirect("/grocery-lists");
    }

    @DeleteMapping(value = "/grocery-list/{id}/delete")
    public void deleteGroceryList(@PathVariable Long id) {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUser(id, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));

        groceryListRepository.delete(groceryList);
    }

    private UserPrincipal getUserPrincipalOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Please log in");
        }

        return (UserPrincipal) auth.getPrincipal();
    }
}
