package ulb.infof307.g07.models.recipe;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;
import ulb.infof307.g07.utils.Config;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestRecipe {
    ArrayList<Ingredient> testData;
    JSONObject instructs;

    @BeforeAll
    public static void setConfigEnv() {
        if (!Config.isPreconfiguredEnvironmentEqualTo(Config.Environment.TESTING)) {
            Config.setEnvironment(Config.Environment.TESTING);
        }
        var currentEnv = Config.getInstance().getEnvironment();
        assert currentEnv == Config.Environment.TESTING;
    }

    @BeforeEach
    public void resetTestData() {
        testData = new ArrayList<Ingredient>();
        testData.add(new Ingredient("some name", 3f, IngredientUnit.UNIT));
        instructs = new JSONObject();
        instructs.put("qqc", "autre chose");
    }

    @Test
    void testRecipeCreatedSuccessfuly() throws RecipeException {
        var recipe = new Recipe("a recipe", 3, testData, instructs, RecipeStyle.VEGGIE, RecipeType.ENTREE, 15);

        assertEquals("a recipe", recipe.getName());
        assertEquals(3, recipe.getNumberPeople());
        assertEquals(testData, recipe.getIngredients());
        assertEquals(RecipeType.ENTREE, recipe.getType());
        assertEquals(15, recipe.getTime());
    }

    @Test
    void testNullNameShouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> new Recipe(null, 3, testData, instructs, RecipeStyle.VEGGIE, RecipeType.ENTREE, 15));
    }

    @Test
    void testNegPeopleShouldThrow() {
        assertThrows(RecipeException.class,
                () -> new Recipe("a recipe", -2, testData, instructs, RecipeStyle.VEGGIE, RecipeType.ENTREE, 15));

    }

    @Test
    void testNegTimeShouldThrow() {
        assertThrows(RecipeException.class,
                () -> new Recipe("a recipe", 1, testData, instructs, RecipeStyle.VEGGIE, RecipeType.ENTREE, -5));

    }

    @Test
    void testNullIngredientsShouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> new Recipe("a recipe", 1, null, new JSONObject(), RecipeStyle.VEGGIE, RecipeType.ENTREE, -5));

    }

    @Test
    void testCheckRecipeNameWorks() throws RecipeException {
        assertTrue(Recipe.getDAO().checkRecipeName("Recipe 1"));
        assertFalse(Recipe.getDAO().checkRecipeName("zerhqze"));
    }

    @Test
    void testCreateRecipeWorks() throws RecipeException {
        Recipe.getDAO().createRecipe(new Recipe("Recipe 40"));
        assertNotNull(Recipe.getDAO().getRecipe("Recipe 40"));
        Recipe.getDAO().deleteRecipeWithName("Recipe 40");
    }

    @Test
    void testCreateRecipeReturnsFalseAndModifiesNothingWithAlreadyPresentRecipe() throws RecipeException {
        var nb = Recipe.getDAO().getAllRecipes().size();
        assertFalse(Recipe.getDAO().createRecipe(new Recipe("Recipe 4")));
        assertEquals(nb, Recipe.getDAO().getAllRecipes().size());
    }

    @Test
    void testAddNewIngredientWorks() throws RecipeException {
        var nb = Recipe.getDAO().getNumberOfIngredient();
        Recipe.getDAO().addNewIngredient("eau");
        assertNotEquals(nb, Recipe.getDAO().getNumberOfIngredient());
    }

    @Test
    void testGetIdFromIngredientWorks() throws RecipeException {
        var nb = Recipe.getDAO().getNumberOfIngredient();
        Recipe.getDAO().addNewIngredient("creme");
        assertNotEquals(0, Recipe.getDAO().getIdFromIngredients("creme"));
    }

    @Test
    void testGetAllRecipesWorks() throws RecipeException {
        assertNotEquals(0, Recipe.getDAO().getAllRecipes().size());
    }

    @Test
    void testGetIngredientNameFromIdWorks() throws RecipeException {
        assertEquals("1", Recipe.getDAO().getIngredientNameFromId(1));
    }

    @Test
    void testDeleteRecipeWithIdWorks() throws RecipeException {
        var nb = Recipe.getDAO().getAllRecipes().size();
        Recipe.getDAO().deleteRecipeWithId(10);
        assertNotEquals(nb, Recipe.getDAO().getAllRecipes().size());
        Recipe.getDAO().createRecipe(new Recipe("Recipe 10"));
    }

    @Test
    void testDeleteRecipeWithNameWorks() throws RecipeException {
        var nb = Recipe.getDAO().getAllRecipes().size();
        Recipe.getDAO().deleteRecipeWithName("Recipe 10");
        assertNotEquals(nb, Recipe.getDAO().getAllRecipes().size());
        Recipe.getDAO().createRecipe(new Recipe("Recipe 10"));
    }

    @Test
    void testDeleteIngredientRecipeWorks() throws RecipeException {
        Recipe.getDAO().deleteIngredientRecipe(3);
        var res = Recipe.getDAO().getIngredientsFromRecipe(3).size();
        assertEquals(0, res);
    }

    @Test
    void testGetIdFromRecipeNameWorks() throws RecipeException {
        assertEquals(1, Recipe.getDAO().getIdFromRecipe("Recipe 1"));
    }

    @Test
    void testUpdateRecipeWorks() throws RecipeException {
        Recipe.getDAO().updateRecipe(new Recipe("Recipe 500"), 5);
        assertNotNull(Recipe.getDAO().getIdFromRecipe("Recipe 500"));
        Recipe.getDAO().updateRecipe(new Recipe("Recipe 5"), 5);
    }

    @Test
    void testGetRecipeFromNameWorks() throws RecipeException {
        assertNotNull(Recipe.getDAO().getRecipe("Recipe 1"));
        assertNull(Recipe.getDAO().getRecipe("gqrhrhreh"));
    }

    @Test
    void testGetRecipeFromIdWorks() throws RecipeException {
        assertNotNull(Recipe.getDAO().getRecipe(1));
        assertNull(Recipe.getDAO().getRecipe(1000000));
    }

    @Test
    void testGetIngredientsFromRecipeIdWorks() throws RecipeException {
        var res = Recipe.getDAO().getIngredientsFromRecipe(6);
        assertEquals("6", res.get(0).getName());
    }

    @Test
    void testGetIngredientsFromRecipeNameWorks() throws RecipeException {
        var res = Recipe.getDAO().getIngredientsFromRecipe("Recipe 6");
        assertEquals("6", res.get(0).getName());
    }

    @Test
    void testGetSuggestionRecipeWorks() throws RecipeException {
        assertNotNull(Recipe.getDAO().getSuggestionRecipe(new Recipe("rh")));
    }

}
