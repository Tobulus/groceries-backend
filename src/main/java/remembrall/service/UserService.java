package remembrall.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;
import remembrall.model.UserDto;
import remembrall.model.repository.UserRepository;
import remembrall.model.validation.EmailExistsException;

import javax.transaction.Transactional;
import java.security.InvalidParameterException;
import java.util.Collections;

@Service
public class UserService {

    @Autowired
    private JdbcUserDetailsManager userDetailsManager;

    @Autowired
    private BCryptPasswordEncoder crypt;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User createUser(UserDto userDto)
            throws EmailExistsException {

        if (userDetailsManager.userExists(userDto.getUsername())) {
            throw new EmailExistsException("There is an account with that email address:" + userDto.getUsername());
        }

        // create basic user
        User user = new User(userDto.getUsername(), crypt.encode(userDto.getPassword()),
                             Collections.singletonList(new SimpleGrantedAuthority("USER")));
        userDetailsManager.createUser(user);

        // update additional fields
        remembrall.model.User entity = userRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new InvalidParameterException("Cannot find previously saved user object: " + user.getUsername()));
        entity.setFirstname(userDto.getFirstname());
        entity.setLastname(userDto.getLastname());
        userRepository.save(entity);

        return user;
    }
}
