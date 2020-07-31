package remembrall.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import remembrall.model.repository.UserRepository;
import remembrall.service.UserPrincipal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LogoutHandler implements LogoutSuccessHandler {

    private UserRepository userRepository;

    @Autowired
    public LogoutHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) {
        userRepository.findById(((UserPrincipal) authentication.getPrincipal()).getUserId()).ifPresent(user -> {
            user.setToken("");
            userRepository.save(user);
        });
    }
}
