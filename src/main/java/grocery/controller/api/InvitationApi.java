package grocery.controller.api;

import grocery.model.GroceryList;
import grocery.model.Invitation;
import grocery.model.User;
import grocery.model.repository.GroceryListRepository;
import grocery.model.repository.InvitationRepository;
import grocery.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class InvitationApi implements BasicApiController {

    @Autowired
    private GroceryListRepository groceryListRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "/api/invitations")
    public List<Invitation> invitations() {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        return invitationRepository.findByReceiverAndAcknowledgedAndDenied(currentUser, false, false);
    }

    @PostMapping(value = "/api/invitation/{id}")
    public Map<String, String> editInvitation(@PathVariable Long id, @RequestParam(required = false) Boolean ack,
                                              @RequestParam(required = false) Boolean deny) {
        Map<String, String> result = new HashMap<>();

        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        Invitation invitation =
                invitationRepository.findByIdAndReceiverAndAcknowledgedAndDenied(id, currentUser, false, false)
                                    .orElseThrow(
                                            () -> new InvalidParameterException("Invitation doesn't exist."));

        if (ack != null) {
            invitation.setAcknowledged(ack);
        }

        if (deny != null) {
            invitation.setDenied(deny);
        }

        invitation = invitationRepository.save(invitation);

        if (invitation.isAcknowledged()) {
            // TODO: can this be done without the additional fetching?
            Long listId = invitation.getGroceryList().getId();
            GroceryList list = groceryListRepository.findEagerUsersById(listId).orElseThrow(
                    () -> new InvalidParameterException("Can't find grocerylist with id " + listId));
            list.getUsers().add(currentUser);
            groceryListRepository.save(list);
        }

        result.put("result", "success");

        return result;
    }
}
