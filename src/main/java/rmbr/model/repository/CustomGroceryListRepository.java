package rmbr.model.repository;

import rmbr.model.GroceryList;
import rmbr.model.User;

import java.util.List;

public interface CustomGroceryListRepository {
    List<GroceryList> fetchLists(User user);
}
