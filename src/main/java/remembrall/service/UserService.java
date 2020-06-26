package remembrall.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
import java.security.SecureRandom;
import java.util.Collections;

@Service
public class UserService {

    private static final char[] VALID_CODE_CHARS =
            {'1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
                    'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'z'};

    @Autowired
    private JdbcUserDetailsManager userDetailsManager;

    @Autowired
    private BCryptPasswordEncoder crypt;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mail;

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

    public void changePassword(String username, String oldPassword, String newPassword) {
        if (!userDetailsManager.userExists(username)) {
            throw new InvalidParameterException("There is no account with that email address:" + username);
        }

        userDetailsManager.changePassword(oldPassword, crypt.encode(newPassword));
    }

    public void resetPassword(String username) {
        if (!userDetailsManager.userExists(username)) {
            throw new InvalidParameterException("There is no account with that email address:" + username);
        }

        String passwd = generatePassword(8);
        User user =
                new User(username, crypt.encode(passwd), Collections.singletonList(new SimpleGrantedAuthority("USER")));
        userDetailsManager.updateUser(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@remembrall.de");
        message.setTo(username);
        message.setSubject("Your password has been reset");
        message.setText("Your new password is: " + passwd);

        mail.send(message);
    }

    private String generatePassword(int length) {
        StringBuilder sb = new StringBuilder();
        SecureRandom rnd = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(VALID_CODE_CHARS[rnd.nextInt(VALID_CODE_CHARS.length)]);
        }
        return sb.toString();
    }
}
