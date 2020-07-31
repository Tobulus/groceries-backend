package remembrall.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.SessionRepositoryFilter;

@Configuration
@Order(1)
public class ApiConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LogoutHandler logoutHandler;

    @Autowired
    private RedisOperationsSessionRepository sessionRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SessionRepositoryFilter<?> filter = new SessionRepositoryFilter<>(sessionRepository);
        filter.setHttpSessionIdResolver(HeaderHttpSessionIdResolver.xAuthToken());

        http.antMatcher("/api/**")
            .addFilterBefore(filter, WebAsyncManagerIntegrationFilter.class)
            .authorizeRequests()
            .antMatchers("/api/user/registration", "/api/user/reset-password").permitAll()
            .anyRequest().authenticated()
            .and()
            .httpBasic()
            .and()
            .csrf()
            .disable()
            .logout()
            .logoutUrl("/api/logout")
            .logoutSuccessHandler(logoutHandler);
    }

}
