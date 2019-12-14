package grocery.service;

import grocery.model.Authority;
import grocery.model.User;
import grocery.model.UserDto;
import grocery.model.repository.AuthorityRepository;
import grocery.model.repository.UserRepository;
import grocery.model.validation.EmailExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Transactional
    @Override
    public User registerNewUserAccount(UserDto accountDto)
            throws EmailExistsException {

        if (emailExists(accountDto.getEmail())) {
            throw new EmailExistsException("There is an account with that email address:" + accountDto.getEmail());
        }

        BCryptPasswordEncoder crypt = new BCryptPasswordEncoder();
        User user = new User();

        user.setUsername(accountDto.getUsername());
        user.setPassword(crypt.encode(accountDto.getPassword()));
        user.setEmail(accountDto.getEmail());
        user.setEnabled(true);

        user = userRepository.save(user);

        Authority auth = new Authority();
        auth.setAuthority("USER");
        auth.setUsername(user.getUsername());

        authorityRepository.save(auth);

        return user;
    }

    private boolean emailExists(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }
}
