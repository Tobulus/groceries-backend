package remembrall.service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
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
            Message message = Message.builder().setToken(receiver.getToken())
                                     .setAndroidConfig(AndroidConfig.builder().setNotification(
                                             AndroidNotification.builder().setClickAction("OPEN_INVITATIONS")
                                                                .setTitle("New Invitation").setBody(
                                                     String.format("%s %s invites you to a list.",
                                                                   sender.getFirstname(),
                                                                   sender.getLastname())).build()).build()).build();

            FirebaseMessaging.getInstance().sendAsync(message);
        }
    }
}
