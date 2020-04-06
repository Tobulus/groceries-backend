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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidParameterException;

@Controller
public class GroceryListController {

    @Autowired
    private GroceryListRepository groceryListRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "/grocery-lists")
    public String groceryLists(Model model) {
        UserPrincipal currentUser = getUserPrincipalOrThrow();
        model.addAttribute("groceryLists",
                           groceryListRepository.findByUsers(userRepository.getOne(currentUser.getUserId())));

        return "/grocery-list/grocery-lists";
    }

    @GetMapping(value = "/grocery-list/{id}")
    public String editGroceryList(@PathVariable Long id, Model model) {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(id, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));
        model.addAttribute("groceryList", groceryList);

        return "/grocery-list/grocery-list";
    }

    @PostMapping(value = "/grocery-list/{id}")
    public void editGroceryList(@PathVariable Long id, @RequestParam String name,
                                HttpServletResponse response) throws IOException {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(id, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));

        groceryList.setName(name);

        groceryListRepository.save(groceryList);

        response.sendRedirect("/grocery-lists");
    }

    @GetMapping(value = "/grocery-list/new")
    public String newGroceryList(Model model) {
        model.addAttribute("groceryList", new GroceryList());
        return "/grocery-list/grocery-list";
    }

    @PostMapping(value = "/grocery-list/new")
    public void newGroceryList(@RequestParam("name") String name, HttpServletResponse response) throws IOException {
        UserPrincipal currentUser = getUserPrincipalOrThrow();

        GroceryList groceryList = new GroceryList();
        groceryList.setName(name);
        groceryList.getUsers().add(userRepository.getOne(currentUser.getUserId()));

        groceryListRepository.save(groceryList);

        response.sendRedirect("/grocery-lists");
    }

    @DeleteMapping(value = "/grocery-list/{id}/delete")
    public void deleteGroceryList(@PathVariable Long id, HttpServletResponse response) throws IOException {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(id, currentUser).orElseThrow(
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
