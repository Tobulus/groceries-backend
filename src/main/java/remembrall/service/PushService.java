package remembrall.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import remembrall.model.User;
import remembrall.model.repository.UserRepository;

import java.security.InvalidParameterException;

@Service
public class PushService {

    @Autowired
    private UserRepository userRepository;

    public void sendInvitationPush(Long receiverId, Long senderId) {
        User receiver = userRepository.findById(receiverId).orElseThrow(
                () -> new InvalidParameterException("Cannot find user with id: " + receiverId));

        if (receiver.getToken() != null && !receiver.getToken().isEmpty()) {
            User sender = userRepository.findById(senderId).orElseThrow(
                    () -> new InvalidParameterException("Cannot find user with id: " + senderId));
            Message message = Message.builder().setToken(receiver.getToken()).setNotification(
                    new Notification("New Invitation",
                                     String.format("%s %s invites you to a list.", sender.getFirstname(),
                                                   sender.getLastname()))).build();

            FirebaseMessaging.getInstance().sendAsync(message);
        }
    }
}
