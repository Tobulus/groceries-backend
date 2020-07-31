package remembrall.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import remembrall.service.UserDetailsManager;

import javax.sql.DataSource;

@EnableTransactionManagement
@EnableWebSecurity
public class WebSecurityConfig {

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bcrypt() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JdbcUserDetailsManager userDetailsManager() {
        JdbcUserDetailsManager manager = new UserDetailsManager(dataSource());
        manager.setUsersByUsernameQuery("select username,password,enabled from users where username=?");
        manager.setAuthoritiesByUsernameQuery("select username,authority from authorities where username = ?");
        return manager;
    }

    @Autowired
    public void initialize(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsManager()).passwordEncoder(bcrypt());
    }

    @Bean
    @Autowired
    public SessionRepository sessionRepository(RedisTemplate<Object, Object> template) {
        return new RedisOperationsSessionRepository(template);
    }

    @Configuration
    @Order(1)
    public static class ApiConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private LogoutHandler logoutHandler;

        @Autowired
        private SessionRepository sessionRepository;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            SessionRepositoryFilter filter = new SessionRepositoryFilter(sessionRepository);
            filter.setHttpSessionIdResolver(HeaderHttpSessionIdResolver.xAuthToken());
            http.antMatcher("/api/**").addFilterBefore(filter,
                                                       WebAsyncManagerIntegrationFilter.class)
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

    @Configuration
    @Order(2)
    public static class WebConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private LogoutHandler logoutHandler;

        @Autowired
        private SessionRepository sessionRepository;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            SessionRepositoryFilter filter = new SessionRepositoryFilter(sessionRepository);
            filter.setHttpSessionIdResolver(new CookieHttpSessionIdResolver());
            http.addFilterBefore(filter,
                                 WebAsyncManagerIntegrationFilter.class)
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
}
