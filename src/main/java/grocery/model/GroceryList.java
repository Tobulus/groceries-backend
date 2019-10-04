package grocery.model;

import javax.persistence.*;

@Entity
@Table(name = "grocerylist")
public class GroceryList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
}
