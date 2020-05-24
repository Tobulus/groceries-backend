package grocery.service;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDetailsManager extends JdbcUserDetailsManager {

    public UserDetailsManager(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected List<UserDetails> loadUsersByUsername(String username) {
        return getJdbcTemplate().query("select id,username,password,enabled,firstname,lastname "
                                       + "from users " + "where username = ?",
                                       new String[]{username}, new RowMapper<UserDetails>() {
                    @Override
                    public UserDetails mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        Long id = rs.getLong(1);
                        String username = rs.getString(2);
                        String password = rs.getString(3);
                        boolean enabled = rs.getBoolean(4);
                        String firstname = rs.getString(5);
                        String lastname = rs.getString(6);
                        return new UserPrincipal(id, username, password, firstname, lastname, enabled, true, true, true,
                                                 AuthorityUtils.NO_AUTHORITIES);
                    }

                });
    }

    @Override
    protected UserDetails createUserDetails(String username,
                                            UserDetails userFromUserQuery, List<GrantedAuthority> combinedAuthorities) {
        return userFromUserQuery;
    }
}
