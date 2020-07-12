package remembrall.model.repository;

import org.springframework.beans.factory.annotation.Autowired;
import remembrall.model.GroceryList;
import remembrall.model.User;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CustomGroceryListRepositoryImpl implements CustomGroceryListRepository {

    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<GroceryList> fetchLists(User user, boolean archived) {
        /*CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<GroceryList> query = cb.createQuery(GroceryList.class);
        Root<GroceryList> root = query.from(GroceryList.class);

        Join<GroceryList, GroceryListEntry> join = root.join("groceryListEntries", JoinType.LEFT);
        Join<GroceryList, User> join1 = root.join("users", JoinType.LEFT);

        query.select(cb.construct(GroceryList.class, root.get("id"), root.get("name"), cb.count(join)));
        query.where(cb.equal(root.get("users"), userRepository.getOne(currentUser.getUserId())));
        query.groupBy(root.get("id"));
        query.groupBy(join.get("groceryList"));

        em.createQuery(query).getResultList();*/

        List<GroceryList> result = new ArrayList<>();
        int archivedInt = archived ? 1 : 0;
        Query
                query = em.createNativeQuery(
                "SELECT gl.id, gl.name, gl.archived, gl.created_by as created_by, gl.created_date as created_date, gl.modified_by as modified_by, gl.modified_date as modified_date, " +
                "COUNT(gle.grocerylist_id) as numberOfEntries, COUNT(case when gle.checked then 1 end) as numberOfCheckedEntries, " +
                "(SELECT GROUP_CONCAT(firstname SEPARATOR ', ') " +
                "FROM " +
                "(SELECT distinct id, firstname " +
                "FROM users " +
                "INNER JOIN users_grocerylists " +
                "WHERE users_grocerylists.grocerylists_id " +
                "IN " +
                "(SELECT users_grocerylists.grocerylists_id as id " +
                "FROM users_grocerylists " +
                "WHERE users_grocerylists.users_id = :user)) lists_of_user) as participants " +
                "FROM grocerylists gl " +
                "LEFT JOIN grocerylistentries gle ON gl.id = gle.grocerylist_id " +
                "INNER JOIN users_grocerylists glu ON gl.id = glu.grocerylists_id " +
                "WHERE glu.users_id = :user AND gl.archived = :archived " +
                "GROUP BY gl.id;", "groceryListWithEntriesInfo");
        query.setParameter("user", user.getId());
        query.setParameter("archived", archivedInt);

        for (Object[] row : (List<Object[]>) query.getResultList()) {
            GroceryList list = (GroceryList) row[0];
            list.setNumberOfEntries(((BigInteger) row[1]).longValue());
            list.setNumberOfCheckedEntries(((BigInteger) row[2]).longValue());
            list.setParticipants(((String) row[3]));
            result.add(list);
        }

        return result;
    }
}
