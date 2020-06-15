package remembrall.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SqlResultSetMapping(name = "groceryListWithEntriesInfo",
        entities = @EntityResult(entityClass = GroceryList.class),
        columns = {@ColumnResult(name = "numberOfEntries"),
                @ColumnResult(name = "numberOfCheckedEntries")})
@NamedEntityGraph(name = "GroceryList.Users", attributeNodes = @NamedAttributeNode(value = "users"))
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "grocerylists")
public class GroceryList extends IdEntity {
    // TODO: cascades
    // TODO: helper methods in order to sync the two models
    // TODO: basic constraints: length, empty, ...

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean archived = false;

    @JsonBackReference
    @OneToMany(mappedBy = "groceryList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroceryListEntry> groceryListEntries = new ArrayList<>();

    @JsonBackReference
    @OneToMany(mappedBy = "groceryList", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Invitation> invitations = new ArrayList<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "users_grocerylists",
            joinColumns = {@JoinColumn(name = "grocerylists_id")},
            inverseJoinColumns = {@JoinColumn(name = "users_id")})
    private Set<User> users = new HashSet<>();

    @JsonBackReference
    @Embedded
    private final Audit audit = new Audit();

    @Transient
    private Long numberOfEntries;

    @Transient
    private Long numberOfCheckedEntries;

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

    public Audit getAudit() {
        return audit;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
