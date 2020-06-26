package remembrall.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import remembrall.controller.BasicController;
import remembrall.model.User;
import remembrall.model.UserDto;
import remembrall.model.repository.UserRepository;
import remembrall.model.validation.EmailExistsException;
import remembrall.service.UserPrincipal;
import remembrall.service.UserService;

import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthenticationApi implements BasicController {

    @Autowired
    private UserService users;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "/api/auth")
    public Map<String, String> auth() {
        Map<String, String> response = new HashMap<>();
        response.put("result", "success");
        return response;
    }

    @PostMapping(value = "/api/user/registration")
    public void registerUserAccount(@Valid UserDto accountDto) throws EmailExistsException {
        users.createUser(accountDto);
    }

    @PutMapping(value = "/api/user/token")
    public void updateToken(@RequestParam String token) {
        UserPrincipal principal = getUserPrincipalOrThrow();
        User user = userRepository.findById(principal.getUserId()).orElseThrow(
                () -> new InvalidParameterException("Cannot find user with Id: " + principal.getUserId()));
        user.setToken(token);
        userRepository.save(user);
    }

    @PutMapping(value = "/api/user/reset-passwd")
    public void resetPassword(@RequestParam String username) {
        users.resetPasswd(username);
    }
}
