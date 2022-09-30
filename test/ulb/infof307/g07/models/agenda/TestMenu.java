package ulb.infof307.g07.models.agenda;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g07.models.agenda.exceptions.MenuException;
import ulb.infof307.g07.models.recipe.*;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;
import ulb.infof307.g07.models.shoppinglist.Product;
import ulb.infof307.g07.utils.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestMenu {
    Recipe tikka = new Recipe("Poulet Tikka", 4, new ArrayList<>(), new JSONObject(), RecipeStyle.MEAT, RecipeType.MEAL,
            45);
    Recipe lasagne = new Recipe("Lasagne", 6, new ArrayList<>(), new JSONObject(), RecipeStyle.MEAT, RecipeType.MEAL,
            120);
    Recipe painPerdu = new Recipe("Pain perdu", 1, new ArrayList<>(), new JSONObject(), RecipeStyle.VEGGIE,
            RecipeType.DESSERT, 10);
    Menu testMenu;
    ArrayList<Recipe> testRecipes;

    public TestMenu() throws RecipeException {
    }

    @BeforeAll
    public static void setConfigEnv() {
        if (!Config.isPreconfiguredEnvironmentEqualTo(Config.Environment.TESTING)) {
            Config.setEnvironment(Config.Environment.TESTING);
        }
        var currentEnv = Config.getInstance().getEnvironment();
        assert currentEnv == Config.Environment.TESTING;
    }

    @BeforeEach
    void resetTestData() {
        testMenu = new Menu("test");
        testRecipes = new ArrayList<>();
    }

    @Test
    void setNameWihNullNameShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testMenu.setName(null));
    }

    @Test
    void setNameWorks() {
        var oldName = "old name";
        var newName = "new name";
        var menu = new Menu(oldName);
        menu.setName(newName);
        assertEquals(menu.getName(), newName);
    }

    @Test
    void testMenuCreationSuccessful() {
        Menu menu = new Menu("Hedbomadaire", new ArrayList<>());
        assertEquals("Hedbomadaire", menu.getName());
    }

    @Test
    void testMenuCreationWithRecipeSuccessful() {
        var menu = new Menu("some name", tikka);
        assertEquals(tikka, menu.getRecipe(0));
    }

    @Test
    void testMenuContructorNullNameShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Menu(null));
    }

    @Test
    void testMenuContructorNullRecipeShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Menu("some name", (Recipe) null));
    }

    @Test
    void testMenuContructorNullRecipeListShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Menu("some name", (List<Recipe>) null));
    }

    @Test
    void testGetAllRecipiesWorks() {
        testMenu.addRecipe(tikka);
        testMenu.addRecipe(painPerdu);
        testRecipes.add(tikka);
        testRecipes.add(painPerdu);
        assertEquals(testRecipes, testMenu.getAllRecipes());
    }

    @Test
    void testMenuAddRecipeWorks() {
        testMenu.addRecipe(tikka);
        testRecipes.add(tikka);
        assertEquals(testRecipes, testMenu.getAllRecipes());
        testMenu.addRecipe(lasagne);
        testRecipes.add(lasagne);
        assertEquals(testRecipes, testMenu.getAllRecipes());
    }

    @Test
    void testAddRecipeWithNullShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testMenu.addRecipe(null));
    }

    @Test
    void testSetRecipesWorks() {
        testRecipes.add(tikka);
        assertNotEquals(testRecipes, testMenu.getAllRecipes());
        testMenu.setRecipes(testRecipes);
        assertEquals(testRecipes, testMenu.getAllRecipes());
    }

    @Test
    void testSetRecipesWithNullShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testMenu.setRecipes(null));
    }

    @Test
    void testDeleteRecipeWithNullShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testMenu.deleteRecipe(null));
    }

    @Test
    void testDeleteRecipeNegativeIndexShouldThrow() {
        int idx = -1;
        assertThrows(MenuException.class, () -> testMenu.deleteRecipe(idx));
    }

    @Test
    void testDeleteRecipeOverflowInderShouldThrow() {
        int idx = testMenu.getNumberRecipe() + 1;
        assertThrows(MenuException.class, () -> testMenu.deleteRecipe(idx));
    }

    @Test
    void testDeleteRecipeWithIndexWorks() throws MenuException {
        ArrayList<Recipe> recipes = new ArrayList<>();
        Menu menu = new Menu("Hedbomadaire", recipes);
        menu.addRecipe(painPerdu);
        menu.addRecipe(lasagne);
        menu.deleteRecipe(0);
        assertEquals(lasagne, menu.getRecipe(0));
    }

    @Test
    void testMenuRemoveRecipe() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        Menu menu = new Menu("Hedbomadaire", recipes);
        menu.addRecipe(painPerdu);
        menu.addRecipe(lasagne);
        menu.deleteRecipe(painPerdu);
        assertEquals(lasagne, menu.getRecipe(0));
    }

    @Test
    void testMenuGetRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        Menu menu = new Menu("Hedbomadaire", recipes);
        menu.addRecipe(tikka);
        menu.addRecipe(lasagne);
        menu.addRecipe(painPerdu);
        ArrayList<Recipe> recipes2 = new ArrayList<>();
        recipes2.add(tikka);
        recipes2.add(lasagne);
        recipes2.add(painPerdu);
        assertEquals(recipes2, menu.getAllRecipes());
    }

    @Test
    public void testGenerateShoppingListWorks() throws MenuException, RecipeException {
        ArrayList<Ingredient> ing = new ArrayList<>();
        ing.add(new Ingredient("mayonnaise"));
        testMenu.addRecipe(new Recipe("recette", 4, ing, new JSONObject(), RecipeStyle.MEAT, RecipeType.MEAL, 10));
        var res = Menu.getDAO().generateShoppingList(testMenu);
        assertTrue(res.hasProduct(new Product("mayonnaise", Product.Category.BAKERY)));

    }
}
