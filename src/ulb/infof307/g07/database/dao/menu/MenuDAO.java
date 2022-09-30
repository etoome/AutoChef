package ulb.infof307.g07.database.dao.menu;

import ulb.infof307.g07.models.agenda.Menu;
import ulb.infof307.g07.models.agenda.exceptions.MenuException;
import ulb.infof307.g07.models.recipe.Recipe;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;

import java.util.ArrayList;

public interface MenuDAO {

    /**
     * Method who create a menu in the DB with an object Menu
     *
     * @param menu
     *            : a menu
     *
     * @return if the menu is created => true else (=> false): there was an error or the menu's name is already taken in
     *         the DB
     */
    boolean createMenu(Menu menu) throws MenuException;

    /**
     * method that gets all menus in the DB
     *
     * @return Arraylist containing all menus
     */
    ArrayList<Menu> getAllMenus() throws MenuException;

    ArrayList<Recipe> getRecipesFromMenu(int id) throws MenuException;

    Integer getIdFromMenu(String menu_name) throws MenuException;

    Menu getMenuFromId(int id) throws MenuException;

    boolean checkMenuName(String menuName) throws MenuException;

    /**
     * delete a menu with a specified id when deleting a menu, we need to delete it from 3 tables : Menu, MenuRecipe and
     * AgendaMenu
     *
     * @param id
     *            : the id of the menu that we want to delete
     */
    void deleteMenu(int id) throws MenuException;

    void deleteMenu(String nameMenu) throws MenuException;

    void deleteMenu(Menu menu) throws MenuException;

    /**
     * Update information for a menu and the related recipes
     *
     * @param id:
     *            id of the menu that we want to update
     * @param menu:
     *            an object menu
     *
     * @return if the menu is updated => true else (=> false): there was an error or the menu's name is already taken in
     *         the DB, so we cannot update
     */
    boolean updateMenu(int id, Menu menu) throws MenuException;

    ShoppingList generateShoppingList(Menu menu) throws MenuException;
}
