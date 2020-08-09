package remembrall.model;

import javax.persistence.*;

@NamedEntityGraph(name = "Invitation.groceryListAndUsers",
        attributeNodes = @NamedAttributeNode(value = "groceryList", subgraph = "users"),
        subgraphs = @NamedSubgraph(name = "users", attributeNodes = @NamedAttributeNode(value = "users")))
@Entity
@Table(name = "invitations")
public class Invitation extends IdEntity {

    @ManyToOne(optional = false)
    private GroceryList groceryList;

    @ManyToOne(optional = false)
    private User sender;

    @ManyToOne(optional = false)
    private User receiver;

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public GroceryList getGroceryList() {
        return groceryList;
    }

    public void setGroceryList(GroceryList groceryList) {
        this.groceryList = groceryList;
    }
}
