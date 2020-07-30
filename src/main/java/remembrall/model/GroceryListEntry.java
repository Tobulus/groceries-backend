package remembrall.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import remembrall.model.enums.quantity_unit.QuantityUnit;
import remembrall.model.enums.quantity_unit.QuantityUnitConverter;
import remembrall.model.enums.quantity_unit.QuantityUnitSerializer;

import javax.persistence.*;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "grocerylistentries")
public class GroceryListEntry extends IdEntity {

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "grocerylist_id")
    private GroceryList groceryList;

    @Column(nullable = false)
    private String name;

    @Column
    private Double quantity;

    @Convert(converter = QuantityUnitConverter.class)
    @JsonSerialize(using = QuantityUnitSerializer.class)
    private QuantityUnit quantityUnit = QuantityUnit.UNDEFINED;

    @Column(nullable = false)
    private Boolean checked = false;

    @JsonBackReference
    @Embedded
    private final Audit audit = new Audit();

    @Transient
    private boolean unseen;

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

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public QuantityUnit getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(QuantityUnit quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public boolean isUnseen() {
        return unseen;
    }

    public void setUnseen(boolean unseen) {
        this.unseen = unseen;
    }
}
