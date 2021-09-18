package remembrall.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import remembrall.controller.BasicController;
import remembrall.model.User;
import remembrall.model.repository.UserRepository;

import java.util.List;

@RestController
public class FriendApi implements BasicController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "/api/friends")
    public List<User> friends(@RequestParam String query) {
        User currentUser = userRepository.getOne(getUserPrincipalOrThrow().getUserId());
        return userRepository.findByIdAndFriends_username(currentUser, query);
    }
}
