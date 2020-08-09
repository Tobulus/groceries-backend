package remembrall.config.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import remembrall.model.User;
import remembrall.model.repository.UserRepository;

public class UserManager implements UserDetailsManager {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void createUser(UserDetails details) {
        User user = new User();
        user.setUsername(details.getUsername());
        user.setPassword(details.getPassword());
        user.setEnabled(details.isEnabled());

        if (details instanceof UserPrincipal) {
            UserPrincipal principal = (UserPrincipal) details;
            user.setFirstname(principal.getFirstname());
            user.setLastname(principal.getLastname());
        }

        userRepository.save(user);
    }

    @Override
    public void updateUser(UserDetails details) {
        userRepository.updatePasswordAndEnabledFor(details.getUsername(), details.getPassword(), details.isEnabled());
    }

    @Override
    public void deleteUser(String username) {
        if (userRepository.deleteByUsername(username) < 1) {
            throw new IllegalArgumentException("Cannot delete username as it cannot be found: " + username);
        }
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext()
                                                          .getAuthentication();

        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context "
                    + "for current user.");
        }

        String username = currentUser.getName();

        // TODO: re-authentication during password change would be placed here
        userRepository.updatePasswordAndEnabledFor(username, newPassword, true);

        SecurityContextHolder.getContext().setAuthentication(
                createNewAuthentication(currentUser, newPassword));
    }

    protected Authentication createNewAuthentication(Authentication currentAuth,
                                                     String newPassword) {
        UserDetails user = loadUserByUsername(currentAuth.getName());

        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());

        return newAuthentication;
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserPrincipal(userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot find user with username: " + username)));
    }
}
