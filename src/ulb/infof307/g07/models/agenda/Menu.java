package ulb.infof307.g07.models.agenda;

import ulb.infof307.g07.database.dao.DAO;
import ulb.infof307.g07.database.dao.menu.MenuDAO;
import ulb.infof307.g07.models.agenda.exceptions.MenuException;
import ulb.infof307.g07.models.recipe.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for the recipes used by the Agenda.
 */
public class Menu {

    private String name;
    private ArrayList<Recipe> recipes;

    public Menu(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Can't create a menu with a null name");
        }
        this.name = name;
        this.recipes = new ArrayList<>();
    }

    public Menu(String name, Recipe recipe) {
        this(name);
        if (recipe == null) {
            throw new IllegalArgumentException("Can't create a menu with a null recipe");
        }
        this.recipes.add(recipe);
    }

    public Menu(String name, List<Recipe> recipes) {
        this(name);
        if (recipes == null) {
            throw new IllegalArgumentException("Can't create a menu with null list of recipe");
        }
        this.recipes = (ArrayList<Recipe>) recipes;
    }

    public int getNumberRecipe() {
        return recipes.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Can't set menu name with null name");
        }
        this.name = name;
    }

    public void setRecipes(List<Recipe> recipes) {
        if (recipes == null) {
            throw new IllegalArgumentException("Can't set null recipes menu array");
        }
        this.recipes = (ArrayList<Recipe>) recipes;
    }

    public void addRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("Can't set null recipe in a menu");
        }
        recipes.add(recipe);
    }

    public void deleteRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("Can't delete a null recipe in a menu");
        }
        recipes.remove(recipe);
    }

    public void deleteRecipe(int index) throws MenuException {
        if (index < 0 || index > getNumberRecipe()) {
            throw new MenuException("Index of the recipe doesn't exist");
        }
        recipes.remove(index);
    }

    public ArrayList<Recipe> getAllRecipes() {
        return recipes;
    }

    public Recipe getRecipe(int i) {
        return recipes.get(i);
    }

    public enum Constants {
        NOT_A_VALID_MENU_ID(-1), NOT_A_VALID_ID_IN_DB(0);

        private final int value;

        Constants(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static MenuDAO getDAO() {
        return DAO.getInstance().getMenuDAO();
    }
}
