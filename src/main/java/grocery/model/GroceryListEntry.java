package grocery.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "grocerylistentries")
public class GroceryListEntry extends IdEntity {
    // TODO: basic constraints: length, empty, ...

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "grocerylist_id")
    private GroceryList groceryList;

    @Column(unique = false, nullable = false)
    private String name;

    private Double quantity;

    @Column(unique = false, nullable = false)
    private Boolean checked = false;

    @JsonBackReference
    @Embedded
    private final Audit audit = new Audit();

    public GroceryList getGroceryList() {
        return groceryList;
    }

    public void setGroceryList(GroceryList groceryList) {
        this.groceryList = groceryList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Audit getAudit() {
        return audit;
    }
}
