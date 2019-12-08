package grocery.service;

import grocery.model.User;
import grocery.model.UserDto;
import grocery.model.validation.EmailExistsException;

public interface IUserService {
    User registerNewUserAccount(UserDto accountDto)
            throws EmailExistsException;
}
