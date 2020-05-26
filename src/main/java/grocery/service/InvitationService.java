package grocery.service;

import grocery.model.GroceryList;
import grocery.model.Invitation;
import grocery.model.User;
import grocery.model.repository.GroceryListRepository;
import grocery.model.repository.InvitationRepository;
import grocery.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.InvalidParameterException;

@Service
public class InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroceryListRepository groceryListRepository;

    @Transactional
    public void ackknowledge(User currentUser, Long id) {
        Invitation invitation =
                invitationRepository.findByIdAndReceiverAndAcknowledgedAndDenied(id, currentUser, false, false)
                                    .orElseThrow(
                                            () -> new InvalidParameterException("Invitation doesn't exist."));

        invitation.setAcknowledged(true);
        invitation = invitationRepository.save(invitation);

        // TODO: can this be done without the additional fetching?
        Long listId = invitation.getGroceryList().getId();
        GroceryList list = groceryListRepository.findEagerUsersById(listId).orElseThrow(
                () -> new InvalidParameterException("Can't find grocerylist with id " + listId));
        list.getUsers().add(currentUser);
        groceryListRepository.save(list);
    }


    public void deny(User currentUser, Long id) {
        Invitation invitation =
                invitationRepository.findByIdAndReceiverAndAcknowledgedAndDenied(id, currentUser, false, false)
                                    .orElseThrow(
                                            () -> new InvalidParameterException("Invitation doesn't exist."));
        invitation.setDenied(true);
        invitationRepository.save(invitation);
    }
}
