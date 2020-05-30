package rmbr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
    @GetMapping(value = "/login")
    public ModelAndView login() {
        ModelAndView view = new ModelAndView();
        view.setViewName("login");
        return view;
    }
}
