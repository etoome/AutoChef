package ulb.infof307.g07.database.dao.shoppinglist;

import ulb.infof307.g07.models.shoppinglist.ShoppingList;
import ulb.infof307.g07.models.shoppinglist.exceptions.ShoppingListException;

import java.util.List;

public interface ShoppingListDAO {

    /**
     * method that gets all shoppingLists in the DB
     *
     * @return Arraylist containing all shoppingLists
     */
    List<ShoppingList> getAllShoppingLists() throws ShoppingListException;

    /**
     * Method that fill shoppingList with related Products
     *
     * @param shoppinglists:
     *            a list of shoppingList
     *
     * @return the same list of shoppingList but now filled with products inside
     */
    List<ShoppingList> fillShoppingListsWithProducts(List<ShoppingList> shoppinglists) throws ShoppingListException;

    ShoppingList fillShoppingListWithProducts(ShoppingList shoppinglist) throws ShoppingListException;

    String getProductNameFromProductShoppingListId(Integer id) throws ShoppingListException;

    int getProductPriceFromProductShoppingListId(Integer id) throws ShoppingListException;

    /**
     * Method who create a shoppingList in the DB with a String name
     *
     * @param name:
     *            the name of the shoppingList
     *
     * @return the shoppingList that we
     *
     * @throws ShoppingListException
     *             just created
     */
    ShoppingList createShoppingList(String name) throws ShoppingListException;

    boolean shoppingListExist(String name) throws ShoppingListException;

    /**
     * Method that update a shoppingList's name
     *
     * @param id:
     *            id of the shoppingList that we want to update
     * @param newName:
     *            new name
     *
     * @return if the shoppingList is updated => true else (=> false): there was an error
     */
    boolean updateShoppingListName(int id, String newName) throws ShoppingListException;

    /**
     * Method that update the status of a shoppingList
     *
     * @param name:
     *            name of the shoppingList
     * @param is_archived:
     *            a boolean depending if we need to archive or un-archived the shoppingList
     */
    void setArchive(String name, boolean is_archived) throws ShoppingListException;

    void deleteShoppingList(String shoppinglistName) throws ShoppingListException;

    Integer getShoppingListIdFromName(String shoppinglistName) throws ShoppingListException;

    /**
     * update the ProductShoppingList with products added in the cart the method is called when we click the button save
     */
    void save(ShoppingList shoppingList) throws ShoppingListException;
}
