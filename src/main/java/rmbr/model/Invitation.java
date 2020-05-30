package rmbr.model;

import javax.persistence.*;
import java.security.InvalidParameterException;

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

    @Column(nullable = false)
    private boolean acknowledged = false;

    @Column(nullable = false)
    private boolean denied = false;

    @PrePersist
    public void validate() {
        if (acknowledged && denied) {
            throw new InvalidParameterException("Invitation cannot be acknowdledge and denied at the same time.");
        }
    }

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

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public GroceryList getGroceryList() {
        return groceryList;
    }

    public void setGroceryList(GroceryList groceryList) {
        this.groceryList = groceryList;
    }

    public boolean isDenied() {
        return denied;
    }

    public void setDenied(boolean denied) {
        this.denied = denied;
    }
}
