package grocery.controller;

import grocery.model.UserDto;
import grocery.model.validation.EmailExistsException;
import grocery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


@RestController
public class RegistrationController {

    @Autowired
    private UserService service;

    @RequestMapping(value = "/user/registration", method = RequestMethod.GET)
    public ModelAndView showRegistrationForm(WebRequest request, Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return new ModelAndView("registration", "user", userDto);
    }

    @RequestMapping(value = "/user/registration", method = RequestMethod.POST)
    public ModelAndView registerUserAccount(
            @ModelAttribute("user") @Valid UserDto accountDto,
            BindingResult result,
            WebRequest request,
            Errors errors) {
        if (result.hasErrors()) {
            return new ModelAndView("registration", "user", accountDto);
        }

        if (createUserAccount(accountDto, result) == null) {
            result.rejectValue("email", "message.regError");
        }

        return new ModelAndView("successRegister", "user", accountDto);

    }

    private User createUserAccount(UserDto accountDto, BindingResult result) {
        try {
            return service.registerNewUserAccount(accountDto);
        } catch (EmailExistsException e) {
            return null;
        }
    }
}
