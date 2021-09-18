package remembrall.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import remembrall.model.GroceryList;
import remembrall.model.Invitation;
import remembrall.model.User;
import remembrall.model.repository.GroceryListRepository;
import remembrall.model.repository.InvitationRepository;
import remembrall.model.repository.UserRepository;

import javax.transaction.Transactional;
import java.security.InvalidParameterException;

@Service
public class InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService users;

    @Autowired
    private GroceryListRepository groceryListRepository;

    @Transactional
    public void ackknowledge(User currentUser, Long id) {
        Invitation invitation =
                invitationRepository.findEagerSenderReceiverByIdAndReceiver(id, currentUser)
                                    .orElseThrow(
                                            () -> new InvalidParameterException("Invitation doesn't exist."));

        Long listId = invitation.getGroceryList().getId();
        GroceryList list = groceryListRepository.findEagerUsersById(listId).orElseThrow(
                () -> new InvalidParameterException("Can't find grocerylist with id " + listId));
        list.getUsers().add(currentUser);
        groceryListRepository.save(list);
        users.createFriendship(invitation.getSender(), invitation.getReceiver());
        invitationRepository.delete(invitation);
    }

    @Transactional
    public void deny(User currentUser, Long id) {
        if (invitationRepository.deleteByIdAndReceiver(id, currentUser) < 1) {
            throw new InvalidParameterException(
                    String.format("There exists no invitation with id %s for receiver %s", id, currentUser.getId()));
        }
    }
}
