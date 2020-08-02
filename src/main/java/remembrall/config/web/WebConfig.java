package remembrall.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.session.web.http.SessionRepositoryFilter;

@Configuration
@Order(2)
public class WebConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/css/**", "/user/registration", "/webfonts/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .permitAll()
            .and()
            .logout()
            .permitAll();
    }

    @Autowired
    public void initAuthFilter(SessionRepositoryFilter<?> sessionRepositoryFilter) {
        // TODO: move this initialization somewhere else
        sessionRepositoryFilter.setHttpSessionIdResolver(new SmartHttpSessionIdResolver());
    }
}
