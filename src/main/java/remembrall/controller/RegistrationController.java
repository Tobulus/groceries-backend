package remembrall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import remembrall.model.UserDto;
import remembrall.model.validation.EmailExistsException;
import remembrall.service.UserService;

import javax.validation.Valid;


@Controller
public class RegistrationController {

    @Autowired
    private UserService service;

    @GetMapping(value = "/user/registration")
    public String showRegistration(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "registration";
    }

    @PostMapping(value = "/user/registration")
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
