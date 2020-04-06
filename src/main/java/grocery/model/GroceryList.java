package grocery.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "grocerylists")
public class GroceryList {
    // TODO: cascades
    // TODO: helper methods in order to sync the two models

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = false, nullable = false)
    private String name;

    @JsonBackReference
    @OneToMany(mappedBy = "groceryList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroceryListEntry> groceryListEntries;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "users_grocerylists",
            joinColumns = {@JoinColumn(name = "grocerylists_id")},
            inverseJoinColumns = {@JoinColumn(name = "users_id")})
    private Set<User> users = new HashSet<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GroceryListEntry> getGroceryListEntries() {
        return groceryListEntries;
    }

    public void setGroceryListEntries(List<GroceryListEntry> groceryListEntries) {
        this.groceryListEntries = groceryListEntries;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
