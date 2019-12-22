package grocery.service;

import grocery.model.UserDto;
import grocery.model.validation.EmailExistsException;
import org.springframework.security.core.userdetails.User;

public interface IUserService {
    User registerNewUserAccount(UserDto accountDto)
            throws EmailExistsException;
}
