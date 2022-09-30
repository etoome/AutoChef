package ulb.infof307.g07.database.dao.menu;

import ulb.infof307.g07.models.agenda.Menu;
import ulb.infof307.g07.models.agenda.exceptions.MenuException;
import ulb.infof307.g07.models.recipe.Ingredient;
import ulb.infof307.g07.models.recipe.exceptions.IngredientException;
import ulb.infof307.g07.models.recipe.Recipe;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuMockDAO implements MenuDAO {
    HashMap<Integer, Menu> idToMenu;

    public MenuMockDAO() {
        idToMenu = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            idToMenu.put(i + 1, new Menu(String.format("Menu %o", i + 1)));
        }
    }

    @Override
    public boolean createMenu(Menu menu) throws MenuException {
        for (Map.Entry<Integer, Menu> entry : idToMenu.entrySet()) {
            if (entry.getValue().getName().equals(menu.getName())) {
                return false;
            }
        }
        idToMenu.put(idToMenu.size() + 1, menu);
        return true;
    }

    @Override
    public ArrayList<Menu> getAllMenus() throws MenuException {
        ArrayList<Menu> res = new ArrayList<>();
        for (Map.Entry<Integer, Menu> entry : idToMenu.entrySet()) {
            res.add(entry.getValue());
        }
        return res;
    }

    @Override
    public ArrayList<Recipe> getRecipesFromMenu(int id) throws MenuException {
        return idToMenu.get(id).getAllRecipes();
    }

    @Override
    public Integer getIdFromMenu(String menu_name) throws MenuException {
        for (Map.Entry<Integer, Menu> entry : idToMenu.entrySet()) {
            if (entry.getValue().getName().equals(menu_name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public Menu getMenuFromId(int id) throws MenuException {
        return idToMenu.get(id);
    }

    @Override
    public boolean checkMenuName(String menuName) throws MenuException {
        for (Map.Entry<Integer, Menu> entry : idToMenu.entrySet()) {
            if (entry.getValue().getName().equals(menuName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteMenu(int id) throws MenuException {
        idToMenu.remove(id);
    }

    @Override
    public void deleteMenu(String nameMenu) throws MenuException {
        for (Map.Entry<Integer, Menu> entry : idToMenu.entrySet()) {
            if (entry.getValue().getName().equals(nameMenu)) {
                idToMenu.remove(entry.getKey());
            }
        }
    }

    @Override
    public void deleteMenu(Menu menu) throws MenuException {
        for (Map.Entry<Integer, Menu> entry : idToMenu.entrySet()) {
            if (menu.equals(entry.getValue())) {
                idToMenu.remove(entry.getKey());
            }
        }
    }

    @Override
    public boolean updateMenu(int id, Menu menu) {
        try {
            if (checkMenuName(menu.getName())) {
                return false;
            }
        } catch (MenuException e) {
            e.printStackTrace();
        }
        idToMenu.put(id, menu);
        return true;
    }

    @Override
    public ShoppingList generateShoppingList(Menu menu) throws MenuException {
        ShoppingList sl = new ShoppingList("generated");
        for (Recipe rec : menu.getAllRecipes()

        ) {
            for (Ingredient ing : rec.getIngredients()) {
                try {
                    sl.addProduct(Ingredient.getDAO().getProductFromIngredient(ing.getName()));
                } catch (IngredientException ignored) {
                    // we skip the product if we can't get it from the ingredient
                }
            }
        }
        return sl;
    }
}
