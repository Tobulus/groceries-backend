package remembrall.config.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import remembrall.service.UserPrincipal;

import java.util.Optional;

/**
 * Provides an auditor which writes audit information to database entities.
 * <p></p>
 * This can be leveraged by using an embedded field in entities, e.g.:
 * <pre>
 *      &#64;JsonBackReference
 *     &#64;Embedded
 *     private final Audit audit = new Audit();
 * </pre>
 */
public class Auditor implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return Optional.of(((UserPrincipal) authentication.getPrincipal()).getUserId());
    }
}
