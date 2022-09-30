package ulb.infof307.g07.models.recipe;

import org.json.JSONObject;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Import and export reciep to JSON, class static with two function public for simplier uses use org.json and javaFX
 */
public class RecipeJSON {

    private static final String INGREDIENT = "ingredient";
    private static RecipeJSON instance;

    public static RecipeJSON getInstance() {
        if (instance == null) {
            instance = new RecipeJSON();
        }
        return instance;
    }

    private RecipeJSON() {
    }

    /**
     * Open a file manager with JavaFX to select a file, and then convert the file a recipe object
     *
     * @return the recipe object extract from JSON
     */
    public Recipe importRecipe(File file) throws RecipeException {
        JSONObject recipeJson;
        try {
            recipeJson = new JSONObject(Files.readString(Paths.get(file.toString())));
            ArrayList<Ingredient> ingredients = new ArrayList<>();
            for (int i = 0; i < recipeJson.getJSONObject(INGREDIENT).length(); i++) {
                JSONObject ingredientsJson = recipeJson.getJSONObject(INGREDIENT);
                JSONObject ingredientJson = ingredientsJson.getJSONObject(String.valueOf(i));
                ingredients.add(new Ingredient(ingredientJson.getString("name"), ingredientJson.getFloat("quantity"),
                        IngredientUnit.getValue(ingredientJson.getString("unit"))));
            }
            return new Recipe(recipeJson.getString("name"), recipeJson.getInt("numberPeople"), ingredients,
                    recipeJson.getJSONObject("instruction"), RecipeStyle.getValue(recipeJson.getString("typeVege")),
                    RecipeType.getValue(recipeJson.getString("type")), recipeJson.getInt("time"));
        } catch (IOException | RecipeException e) {
            throw new RecipeException("Error when import a recipe from a json file", e);
        }
    }

    /**
     * Open a file manager with JavaFX to know where the file is going to be saved convert a Recipe object to a
     * JSONObject and saved it
     *
     * @param recipe:
     *            recipe object already check to saved
     *
     * @return boolean, if it works or not
     */
    public boolean exportRecipe(Recipe recipe, File file) throws IOException {
        JSONObject recipeJson = new JSONObject();
        recipeJson.put("name", recipe.getName());
        recipeJson.put("numberPeople", recipe.getNumberPeople());
        recipeJson.put("instruction", recipe.getInstruction());
        recipeJson.put("typeVege", recipe.getStyle().toString());
        recipeJson.put("type", recipe.getType().toString());
        recipeJson.put("time", recipe.getTime());

        JSONObject ingredientsJson = new JSONObject();

        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            JSONObject ingredientJson = new JSONObject();
            ingredientJson.put("name", recipe.getIngredients().get(i).getName());
            ingredientJson.put("quantity", recipe.getIngredients().get(i).getQuantityValue());
            ingredientJson.put("unit", recipe.getIngredients().get(i).getUnit().toString());
            ingredientsJson.put(String.valueOf(i), ingredientJson);
        }
        recipeJson.put(INGREDIENT, ingredientsJson);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(recipeJson.toString(2));
        } catch (IOException e) {
            throw new IOException(e);
        }
        return true;
    }
}
