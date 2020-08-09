package remembrall.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import remembrall.config.user.UserPrincipal;
import remembrall.model.User;
import remembrall.model.UserDto;
import remembrall.model.repository.UserRepository;
import remembrall.model.validation.EmailExistsException;

import java.security.InvalidParameterException;
import java.security.SecureRandom;

@Service
public class UserService {

    private static final char[] VALID_CODE_CHARS =
            {'1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
                    'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'z'};

    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private BCryptPasswordEncoder crypt;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mail;

    @Transactional
    public void createUser(UserDto userDto)
            throws EmailExistsException {

        if (userDetailsManager.userExists(userDto.getUsername())) {
            throw new EmailExistsException(
                    "There is already an account with that email address:" + userDto.getUsername());
        }

        User dummy = new User();
        dummy.setLastname(userDto.getLastname());
        dummy.setFirstname(userDto.getFirstname());
        dummy.setUsername(userDto.getUsername());
        dummy.setPassword(crypt.encode(userDto.getPassword()));
        dummy.setEnabled(true);
        UserPrincipal userDetails = new UserPrincipal(dummy);

        userDetailsManager.createUser(userDetails);
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (!userDetailsManager.userExists(username)) {
            throw new InvalidParameterException("There is no account with that email address:" + username);
        }

        userDetailsManager.changePassword(oldPassword, crypt.encode(newPassword));
    }

    @Transactional
    public void resetPassword(String username) {
        if (!userDetailsManager.userExists(username)) {
            throw new InvalidParameterException("There is no account with that email address:" + username);
        }

        String passwd = generatePassword(8);
        User dummy = new User();
        dummy.setUsername(username);
        dummy.setPassword(crypt.encode(passwd));
        dummy.setEnabled(true);
        UserPrincipal userDetails = new UserPrincipal(dummy);
        userDetailsManager.updateUser(userDetails);

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
