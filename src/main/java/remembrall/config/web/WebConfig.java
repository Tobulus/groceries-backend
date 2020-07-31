package remembrall.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.SessionRepositoryFilter;

@Configuration
@Order(2)
public class WebConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LogoutHandler logoutHandler;

    @Autowired
    private RedisOperationsSessionRepository sessionRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SessionRepositoryFilter<?> filter = new SessionRepositoryFilter<>(sessionRepository);
        filter.setHttpSessionIdResolver(new CookieHttpSessionIdResolver());

        http.addFilterBefore(filter, WebAsyncManagerIntegrationFilter.class)
            .authorizeRequests()
            .antMatchers("/css/**", "/user/registration", "/webfonts/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .permitAll()
            .and()
            .logout()
            .logoutSuccessHandler(logoutHandler)
            .permitAll();
    }
}
