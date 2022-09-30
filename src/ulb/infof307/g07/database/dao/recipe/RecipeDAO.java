package ulb.infof307.g07.database.dao.recipe;

import ulb.infof307.g07.models.recipe.Ingredient;
import ulb.infof307.g07.models.recipe.Recipe;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;

import java.util.ArrayList;

public interface RecipeDAO {
    boolean checkRecipeName(String name) throws RecipeException;

    /**
     * Method who create a recipe in the DB with an object Recipe
     *
     * @param recipe:
     *            a recipe
     *
     * @return if the recipe is created => true else (=> false): there was an error or the recipe's name is already
     *         taken in the DB or the recipe's name is already in the table Menu (not allowed because a Recipe is always
     *         a menu)
     */
    boolean createRecipe(Recipe recipe) throws RecipeException;

    int addNewIngredient(String ingredient) throws RecipeException;

    int getNumberOfIngredient() throws RecipeException;

    Integer getIdFromIngredients(String ingredient) throws RecipeException;

    /**
     * method that gets all recipes in the DB
     *
     * @return Arraylist containing all recipes
     */
    ArrayList<Recipe> getAllRecipes() throws RecipeException;

    String getIngredientNameFromId(int id) throws RecipeException;

    void deleteRecipeWithId(int id) throws RecipeException;

    void deleteRecipeWithName(String name) throws RecipeException;

    void deleteIngredientRecipe(int id) throws RecipeException;

    Integer getIdFromRecipe(String name) throws RecipeException;

    /**
     * Update information for a recipe and the related ingredients
     *
     * @param id:
     *            id of the recipe that we want to update
     * @param recipe:
     *            an object recipe
     *
     * @return if the recipe is updated => true else (=> false): there was an error or the recipe's name is already
     *         taken in the table recipe or Menu (A recipe is a Menu but a Menu is not always a recipe), so we cannot
     *         update
     */
    boolean updateRecipe(Recipe recipe, int id) throws RecipeException;

    Recipe getRecipe(String name) throws RecipeException;

    /**
     * Method that gets a Recipe from an id
     *
     * @param id:
     *            id of the recipe that we want
     *
     * @return an object Recipe
     */
    Recipe getRecipe(int id) throws RecipeException;

    ArrayList<Ingredient> getIngredientsFromRecipe(String name) throws RecipeException;

    /**
     * Method that gets all ingredients from a Recipe
     *
     * @param id:
     *            id of recipe
     *
     * @return Arraylist containing all the ingredients name
     */
    ArrayList<Ingredient> getIngredientsFromRecipe(int id) throws RecipeException;

    /**
     * Method that gets a suggestion from a Recipe The suggestion cannot be the same recipe and cannot be a DESSERT,
     * DRINK OR ENTREE
     *
     * @param recipe:
     *            actual recipe
     *
     * @return an random recipe
     */
    Recipe getSuggestionRecipe(Recipe recipe) throws RecipeException;
}
