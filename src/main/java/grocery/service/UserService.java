package grocery.service;

import grocery.model.UserDto;
import grocery.model.repository.AuthorityRepository;
import grocery.model.repository.UserRepository;
import grocery.model.validation.EmailExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;

@Service
public class UserService implements IUserService {

    @Autowired
    private JdbcUserDetailsManager userDetailsManager;

    @Autowired
    private BCryptPasswordEncoder crypt;

    @Transactional
    @Override
    public User registerNewUserAccount(UserDto userDto)
            throws EmailExistsException {

        if (userDetailsManager.userExists(userDto.getUsername())) {
            throw new EmailExistsException("There is an account with that email address:" + userDto.getUsername());
        }
        User user = new User(userDto.getUsername(), crypt.encode(userDto.getPassword()), Collections.singletonList(new SimpleGrantedAuthority("USER")));
        userDetailsManager.createUser(user);

        return user;
    }
}
