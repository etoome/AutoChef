package ulb.infof307.g07.database.dao.menu;

import ulb.infof307.g07.database.Database;
import ulb.infof307.g07.database.DatabaseException;
import ulb.infof307.g07.models.agenda.Menu;
import ulb.infof307.g07.models.agenda.exceptions.MenuException;
import ulb.infof307.g07.models.recipe.Ingredient;
import ulb.infof307.g07.models.recipe.exceptions.IngredientException;
import ulb.infof307.g07.models.recipe.Recipe;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MenuDefaultDAO implements MenuDAO {
    @Override
    public boolean createMenu(Menu menu) throws MenuException {
        var db = Database.getInstance();

        try {
            if (Recipe.getDAO().checkRecipeName(menu.getName()) && Menu.getDAO().checkMenuName(menu.getName())) {
                return false;
            }
            var currentMenuId = db.query("INSERT INTO Menu(name) VALUES(?);", List.of(menu.getName()),
                    Statement.RETURN_GENERATED_KEYS, result -> result.next() ? result.getInt(1) : null);

            int currentRecipeId;
            for (Recipe recipe : menu.getAllRecipes()) {
                currentRecipeId = Recipe.getDAO().getIdFromRecipe(recipe.getName());
                if (currentRecipeId != Menu.Constants.NOT_A_VALID_ID_IN_DB.getValue()) { // If recipe exist in the
                    // db
                    db.query("INSERT INTO MenuRecipe(menu_id, recipe_id) VALUES(?, ?);",
                            List.of(currentMenuId, currentRecipeId));
                } else {
                    Recipe.getDAO().createRecipe(recipe);
                }
            }
            return true;
        } catch (DatabaseException | RecipeException e) {
            throw new MenuException("Error when creating a menu ", e);
        }
    }

    @Override
    public ArrayList<Menu> getAllMenus() throws MenuException {
        var db = Database.getInstance();
        var menuNull = new ArrayList<Menu>();

        try {
            var ids = new ArrayList<Integer>();
            var menus = new ArrayList<Menu>();
            db.query("""
                    SELECT id, name FROM Menu INNER JOIN MenuRecipe ON id = MenuRecipe.menu_id
                    GROUP BY
                    id
                    HAVING COUNT(*) > 1""", result -> {
                while (result.next()) {
                    ids.add(result.getInt("id"));
                    menus.add(new Menu(result.getString("name"), new ArrayList<>()));
                }
                return menuNull;
            });
            // selecting the Recipes ID
            for (int id : ids) {
                menus.get(ids.indexOf(id)).setRecipes(getRecipesFromMenu(id));
            }
            return menus;
        } catch (DatabaseException | MenuException e) {
            throw new MenuException("Error when getting all menus ", e);
        }
    }

    @Override
    public ArrayList<Recipe> getRecipesFromMenu(int id) throws MenuException {
        var db = Database.getInstance();
        ArrayList<Recipe> res = new ArrayList<>();

        try {
            boolean isOk = db.query("SELECT recipe_id FROM MenuRecipe WHERE menu_id=?;", List.of(id), result -> {
                while (result.next()) {
                    try {
                        res.add(Recipe.getDAO().getRecipe(result.getInt("recipe_id")));
                    } catch (RecipeException e) {
                        return false;
                    }
                }
                return true;
            });
            if (!isOk) {
                throw new MenuException("Error while getting recipes from the menu");
            }
            return res;
        } catch (DatabaseException e) {
            throw new MenuException("Error when getting recipes from a menu ", e);
        }
    }

    @Override
    public Integer getIdFromMenu(String menu_name) throws MenuException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT id FROM Menu WHERE name=?;", List.of(menu_name),
                    result -> result.next() ? result.getInt("id") : Menu.Constants.NOT_A_VALID_MENU_ID.getValue());
        } catch (DatabaseException ignored) {
            return Menu.Constants.NOT_A_VALID_MENU_ID.getValue();
        }
    }

    @Override
    public Menu getMenuFromId(int id) throws MenuException {
        var db = Database.getInstance();

        try {
            var menu = db.query("SELECT * FROM Menu WHERE id = ?", List.of(id), result -> {
                try {
                    return result.next() ? new Menu(result.getString("name"), getRecipesFromMenu(id)) : null;
                } catch (MenuException e) {
                    return null;
                }
            });
            if (menu == null) {
                throw new MenuException("There is no menu corresponding to the ID");
            }
            return menu;
        } catch (DatabaseException e) {
            throw new MenuException("Error when getting menu from id ", e);
        }
    }

    @Override
    public boolean checkMenuName(String menuName) throws MenuException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT id FROM Menu WHERE name=?;", List.of(menuName), ResultSet::next);
        } catch (DatabaseException ignored) {
            return false; // if we change this to throw an error, we need to do a try catch in our if(check)
        }
    }

    @Override
    public void deleteMenu(int id) throws MenuException {
        var db = Database.getInstance();
        try {
            db.begin();
            db.query("DELETE FROM Menu WHERE id=?", List.of(id));
            db.query("DELETE FROM MenuRecipe WHERE menu_id=?", List.of(id));
            db.query("DELETE FROM AgendaMenu WHERE menu_id=?", List.of(id));
            db.commit();
        } catch (DatabaseException e) {
            throw new MenuException("Error when deleting menu ", e);
        }
    }

    @Override
    public void deleteMenu(String nameMenu) throws MenuException {
        deleteMenu(getIdFromMenu(nameMenu));
    }

    @Override
    public void deleteMenu(Menu menu) throws MenuException {
        deleteMenu(menu.getName());
    }

    @Override
    public boolean updateMenu(int id, Menu menu) throws MenuException {
        var db = Database.getInstance();

        try {
            // Check if this name is already taken or not
            if (checkMenuName(menu.getName()) && Recipe.getDAO().checkRecipeName(menu.getName())
                    && getIdFromMenu(menu.getName()) != id) {
                return false;
            }
            db.begin();
            // Update Menu
            db.query("UPDATE Menu SET name=? WHERE id=?;", List.of(menu.getName(), id));
            db.query("DELETE FROM MenuRecipe WHERE menu_id=?;", List.of(id));

            // Insert into MenuRecipe
            for (Recipe recipe : menu.getAllRecipes()) {
                db.query("INSERT INTO MenuRecipe(menu_id, recipe_id) VALUES(?,?);",
                        List.of(id, Recipe.getDAO().getIdFromRecipe(recipe.getName())));
            }
            db.commit();
            return true;
        } catch (DatabaseException | RecipeException e) {
            throw new MenuException("Error when updating menu ", e);
        }
    }

    @Override
    public ShoppingList generateShoppingList(Menu menu) throws MenuException {
        ShoppingList res = new ShoppingList("Liste de course du menu " + menu.getName());
        Integer id_menu;
        ArrayList<Recipe> recipes;

        id_menu = getIdFromMenu(menu.getName());
        if (id_menu == Menu.Constants.NOT_A_VALID_MENU_ID.getValue()) {
            throw new MenuException("Error when getting id from menu");
        }
        recipes = getRecipesFromMenu(id_menu);
        for (Recipe recipe : recipes) {
            try {
                var ingredients = Recipe.getDAO().getIngredientsFromRecipe(recipe.getName());
                for (Ingredient ingredient : ingredients) {
                    res.addProduct(Ingredient.getDAO().getProductFromIngredient(ingredient.getName()),
                            ingredient.getQuantity());
                }
            } catch (RecipeException | IngredientException e) {
                throw new MenuException(
                        "Error when getting ingredients from recipe OR when getting product from ingredient", e);
            }
        }
        return res;
    }
}
