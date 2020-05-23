package grocery.model.repository;

import grocery.model.GroceryList;
import grocery.model.User;

import java.util.List;

public interface CustomGroceryListRepository {
    List<GroceryList> fetchLists(User user);
}
