package grocery.controller;

import grocery.model.GroceryList;
import grocery.model.GroceryListEntry;
import grocery.model.User;
import grocery.model.repository.GroceryListEntryRepository;
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
import java.util.List;

@Controller
public class GroceryListEntryController {

    // TODO: better user checking

    @Autowired
    private GroceryListRepository groceryListRepository;

    @Autowired
    private GroceryListEntryRepository groceryListEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "/grocery-list/{id}/entries")
    public String listGroceryListEntries(@PathVariable Long id, Model model) {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(id, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));
        List<GroceryListEntry> entries = groceryListEntryRepository.findByGroceryList(groceryList);

        model.addAttribute("groceryListEntries", entries);

        return "/grocery-list-entry/grocery-list-entries";
    }

    @GetMapping(value = "/grocery-list/{id}/entry/new")
    public String newGroceryListEntry(@PathVariable Long id, Model model) {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(id, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));

        model.addAttribute("groceryList", groceryList);
        model.addAttribute("groceryListEntry", new GroceryListEntry());

        return "/grocery-list-entry/grocery-list-entry";
    }

    @GetMapping(value = "/grocery-list/{listId}/entry/{entryId}")
    public String editGroceryListEntry(@PathVariable Long listId, @PathVariable Long entryId, Model model) {
        GroceryListEntry groceryListEntry = fetchGroceryListEntry(listId, entryId);

        model.addAttribute("groceryList", groceryListEntry.getGroceryList());
        model.addAttribute("groceryListEntry", groceryListEntry);

        return "/grocery-list-entry/grocery-list-entry";
    }

    @PostMapping(value = "/grocery-list/{id}/entry/new")
    public void newGroceryListEntry(@RequestParam("name") String name, @PathVariable Long id,
                                    HttpServletResponse response) throws IOException {
        GroceryListEntry groceryListEntry = new GroceryListEntry();
        groceryListEntry.setGroceryList(groceryListRepository.getOne(id));
        groceryListEntry.setName(name);

        groceryListEntryRepository.save(groceryListEntry);

        response.sendRedirect("/grocery-list/" + id + "/entries");
    }

    @PostMapping(value = "/grocery-list/{listId}/entry/{entryId}")
    public void editGroceryListEntry(@RequestParam("name") String name, @PathVariable Long listId,
                                     @PathVariable Long entryId, HttpServletResponse response) throws IOException {
        GroceryListEntry groceryListEntry = fetchGroceryListEntry(listId, entryId);
        groceryListEntry.setName(name);

        groceryListEntryRepository.save(groceryListEntry);

        response.sendRedirect("/grocery-list/" + listId + "/entries");
    }

    @DeleteMapping(value = "/grocery-list/{listId}/entry/{entryId}/delete")
    public void deleteGroceryListentry(@PathVariable Long listId, @PathVariable Long entryId,
                                       HttpServletResponse response) throws IOException {
        GroceryListEntry listEntry = fetchGroceryListEntry(listId, entryId);
        groceryListEntryRepository.delete(listEntry);
    }

    private GroceryListEntry fetchGroceryListEntry(Long groceryListId, Long entryId) {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        GroceryList groceryList = groceryListRepository.findByIdAndUsers(groceryListId, currentUser).orElseThrow(
                () -> new InvalidParameterException("List doesn't exist"));
        return groceryListEntryRepository.findByGroceryListAndId(groceryList, entryId);
    }

    private UserPrincipal getUserPrincipalOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Please log in");
        }

        return (UserPrincipal) auth.getPrincipal();
    }
}
