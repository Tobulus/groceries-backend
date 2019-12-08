package grocery.security;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

// TODO: needed?
public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {
    public SecurityInitializer() {
        super(WebSecurityConfig.class);
    }
}
