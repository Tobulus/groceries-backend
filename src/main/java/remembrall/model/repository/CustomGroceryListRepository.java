package remembrall.model.repository;

import remembrall.model.GroceryList;
import remembrall.model.User;

import java.util.List;

public interface CustomGroceryListRepository {
    List<GroceryList> fetchLists(User user);
}
