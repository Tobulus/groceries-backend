package remembrall.model;

import javax.persistence.*;
import java.security.InvalidParameterException;

@NamedEntityGraph(name = "Invitation.groceryListAndUsers",
        attributeNodes = @NamedAttributeNode(value = "groceryList", subgraph = "users"),
        subgraphs = @NamedSubgraph(name = "users", attributeNodes = @NamedAttributeNode(value = "users")))
@NamedEntityGraph(name = "Invitation.receiver", attributeNodes = @NamedAttributeNode(value = "receiver"))
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

    @Column(nullable = false)
    private boolean pushedReminder = false;

    private Long createdTimestamp;

    @PrePersist
    public void validate() {
        if (acknowledged && denied) {
            throw new InvalidParameterException("Invitation cannot be acknowdledge and denied at the same time.");
        }
        createdTimestamp = System.currentTimeMillis();
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

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public boolean isPushedReminder() {
        return pushedReminder;
    }

    public void setPushedReminder(boolean pushedReminder) {
        this.pushedReminder = pushedReminder;
    }
}
