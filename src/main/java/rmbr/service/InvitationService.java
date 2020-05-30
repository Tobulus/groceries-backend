package rmbr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rmbr.model.GroceryList;
import rmbr.model.Invitation;
import rmbr.model.User;
import rmbr.model.repository.GroceryListRepository;
import rmbr.model.repository.InvitationRepository;
import rmbr.model.repository.UserRepository;

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
