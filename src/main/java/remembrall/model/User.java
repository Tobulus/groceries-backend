package remembrall.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
public class User extends IdEntity {

    @Column(unique = true, nullable = false)
    private String username;

    private String firstname;

    private String lastname;

    @JsonIgnore
    private String token;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(length = 2)
    private String lang;

    @Column(nullable = false)
    private boolean enabled;

    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    private Set<GroceryList> groceryLists = new HashSet<>();

    @JsonIgnore
    @OneToMany
    @JoinTable(name = "friends", joinColumns = {
            @JoinColumn(name = "user", referencedColumnName = "id"),
            @JoinColumn(name = "friend", referencedColumnName = "id")})
    private Set<User> friends = new HashSet<>();

    public Set<User> getFriends() {
        return friends;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<GroceryList> getGroceryLists() {
        return groceryLists;
    }

    public void setGroceryLists(Set<GroceryList> groceryLists) {
        this.groceryLists = groceryLists;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLang() {
        return lang;
    }

    public Locale getLocale() {
        if (lang != null) {
            return Locale.forLanguageTag(lang);
        }
        return Locale.ENGLISH;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
