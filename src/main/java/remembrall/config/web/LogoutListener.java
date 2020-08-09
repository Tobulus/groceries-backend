package remembrall.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;
import remembrall.config.user.UserPrincipal;
import remembrall.model.repository.UserRepository;

@Component
public class LogoutListener implements ApplicationListener<SessionDestroyedEvent> {

    private final UserRepository userRepository;

    @Autowired
    public LogoutListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {
        userRepository.findById(
                ((UserPrincipal) event.getSecurityContexts().get(0).getAuthentication().getPrincipal()).getUserId())
                      .ifPresent(user -> {
                          user.setToken("");
                          userRepository.save(user);
                      });
    }
}
