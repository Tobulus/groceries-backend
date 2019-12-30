package grocery.config;

import grocery.service.UserDetailsManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/css/**", "/user/registration", "/webfonts/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/log-in")
                .loginProcessingUrl("/login")
                //.successForwardUrl("/grocery-lists")
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsManager()).passwordEncoder(bcrypt());
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
}
