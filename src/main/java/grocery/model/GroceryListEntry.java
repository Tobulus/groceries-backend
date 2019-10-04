package grocery.model;

import javax.persistence.*;

@Entity
@Table(name = "grocerylistentry")
public class GroceryListEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
}
