package grocery.controller;

import grocery.model.UserDto;
import grocery.model.validation.EmailExistsException;
import grocery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;


@Controller
public class RegistrationController {

    @Autowired
    private UserService service;

    @RequestMapping(value = "/user/registration", method = RequestMethod.GET)
    public String showRegistration(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "registration";
    }

    @RequestMapping(value = "/user/registration", method = RequestMethod.POST)
    public String registerUserAccount(
            @ModelAttribute("user") @Valid UserDto accountDto,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", accountDto);
            return "registration";
        }

        if (createUserAccount(accountDto) == null) {
            result.rejectValue("email", "message.regError");
        }

        model.addAttribute("user", accountDto);
        return "/grocery-list/grocery-lists";

    }

    private User createUserAccount(UserDto accountDto) {
        try {
            return service.createUser(accountDto);
        } catch (EmailExistsException e) {
            return null;
        }
    }
}
