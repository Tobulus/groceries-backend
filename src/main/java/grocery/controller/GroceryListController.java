package grocery.controller;

import grocery.model.GroceryList;
import grocery.model.Invitation;
import grocery.model.User;
import grocery.model.repository.GroceryListRepository;
import grocery.model.repository.InvitationRepository;
import grocery.model.repository.UserRepository;
import grocery.service.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidParameterException;

@Controller
public class GroceryListController implements BasicController {

    // TODO: check whether the additional fetching of objects is already done by table joins via hibernate

    @Autowired
    private GroceryListRepository groceryListRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @GetMapping(value = "/grocery-lists")
    public String groceryLists(Model model) {
        UserPrincipal currentUser = getUserPrincipalOrThrow();

        model.addAttribute("groceryLists",
                           groceryListRepository.fetchLists(userRepository.getOne(currentUser.getUserId())));

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

    @PostMapping(value = "/grocery-list/{id}/invite")
    public void share(@PathVariable Long id, @RequestParam Long user, HttpServletResponse response) throws IOException {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(id, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));
        User receiver = userRepository.findById(user).orElseThrow(
                () -> new IllegalArgumentException("The invited user doesn't exist."));

        Invitation invitation = new Invitation();
        invitation.setSender(currentUser);
        invitation.setReceiver(receiver);
        invitation.setGroceryList(groceryList);
        invitationRepository.save(invitation);

        response.sendRedirect("/grocery-list/" + id);
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
    public void deleteGroceryList(@PathVariable Long id) {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(id, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));

        groceryListRepository.delete(groceryList);
    }
}
