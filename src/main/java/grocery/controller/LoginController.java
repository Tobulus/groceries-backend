package grocery.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class LoginController {

    @RequestMapping(value = "/log-in", method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView view = new ModelAndView();
        view.setViewName("login");
        return view;
    }
}
