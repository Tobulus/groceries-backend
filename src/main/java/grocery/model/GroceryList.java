package grocery.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NamedEntityGraph(name = "GroceryList.Users", attributeNodes = @NamedAttributeNode(value = "users"))
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "grocerylists")
public class GroceryList extends Audit {
    // TODO: cascades
    // TODO: helper methods in order to sync the two models
    // TODO: basic constraints: length, empty, ...

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = false, nullable = false)
    private String name;

    @JsonBackReference
    @OneToMany(mappedBy = "groceryList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroceryListEntry> groceryListEntries = new ArrayList<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "users_grocerylists",
            joinColumns = {@JoinColumn(name = "grocerylists_id")},
            inverseJoinColumns = {@JoinColumn(name = "users_id")})
    private Set<User> users = new HashSet<>();

    @Transient
    private Long numberOfEntries;
    @Transient
    private Long numberOfCheckedEntries;

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

    public Long getNumberOfEntries() {
        return numberOfEntries;
    }

    public void setNumberOfEntries(Long numberOfEntries) {
        this.numberOfEntries = numberOfEntries;
    }

    public Long getNumberOfCheckedEntries() {
        return numberOfCheckedEntries;
    }

    public void setNumberOfCheckedEntries(Long numberOfCheckedEntries) {
        this.numberOfCheckedEntries = numberOfCheckedEntries;
    }
}
