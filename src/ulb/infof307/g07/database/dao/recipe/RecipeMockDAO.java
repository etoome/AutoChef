package ulb.infof307.g07.database.dao.recipe;

import ulb.infof307.g07.models.recipe.Ingredient;
import ulb.infof307.g07.models.recipe.Recipe;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipeMockDAO implements RecipeDAO {
    HashMap<Integer, Recipe> idToRecipe;
    HashMap<String, Recipe> nameToRecipe;
    HashMap<Integer, Ingredient> ingredients;

    public RecipeMockDAO() {
        idToRecipe = new HashMap<>();
        nameToRecipe = new HashMap<>();
        ingredients = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            try {
                var temp = new Recipe(String.format("Recipe %o", i + 1));
                var list = new ArrayList<Ingredient>();
                ingredients.put(i + 1, new Ingredient(String.valueOf(i + 1)));
                list.add(new Ingredient(String.valueOf(i + 1)));
                temp.setIngredients(list);
                idToRecipe.put(i + 1, temp);
                nameToRecipe.put(String.format("Recipe %o", i + 1), temp);
            } catch (RecipeException ignored) {
            }

        }
    }

    @Override
    public boolean checkRecipeName(String name) {
        return nameToRecipe.containsKey(name);
    }

    @Override
    public boolean createRecipe(Recipe recipe) {
        if (checkRecipeName(recipe.getName())) {
            return false;
        }
        idToRecipe.put(idToRecipe.size() + 1, recipe);
        nameToRecipe.put(recipe.getName(), recipe);
        return true;
    }

    @Override
    public int addNewIngredient(String ingredient) {
        var s = ingredients.size() + 1;
        ingredients.put(s, new Ingredient(ingredient));
        return s;
    }

    @Override
    public int getNumberOfIngredient() {
        return ingredients.size();
    }

    @Override
    public Integer getIdFromIngredients(String ingredient) {
        for (Map.Entry<Integer, Ingredient> entry : ingredients.entrySet()) {
            if (entry.getValue().getName().equals(ingredient)) {
                return entry.getKey();
            }
        }
        return 0;
    }

    @Override
    public ArrayList<Recipe> getAllRecipes() {
        return new ArrayList<>(idToRecipe.values());
    }

    @Override
    public String getIngredientNameFromId(int id) {
        return ingredients.get(id).getName();
    }

    @Override
    public void deleteRecipeWithId(int id) {
        var toDelete = idToRecipe.get(id);
        idToRecipe.remove(id);
        nameToRecipe.remove(toDelete.getName());
    }

    @Override
    public void deleteRecipeWithName(String name) {
        var toDelete = nameToRecipe.get(name);
        nameToRecipe.remove(name);
        for (Map.Entry<Integer, Recipe> entry : idToRecipe.entrySet()) {
            if (entry.getValue().getName().equals(toDelete.getName())) {
                idToRecipe.remove(entry.getKey());
                return;
            }
        }

    }

    @Override
    public void deleteIngredientRecipe(int id) {
        var r = idToRecipe.get(id);
        r.setIngredients(new ArrayList<>());
    }

    @Override
    public Integer getIdFromRecipe(String name) {
        for (Map.Entry<Integer, Recipe> entry : idToRecipe.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public boolean updateRecipe(Recipe recipe, int id) {
        if (idToRecipe.get(id) == null) {
            return false;
        }
        nameToRecipe.remove(idToRecipe.get(id).getName());
        idToRecipe.put(id, recipe);
        nameToRecipe.put(recipe.getName(), recipe);
        return true;
    }

    @Override
    public Recipe getRecipe(String name) {
        return nameToRecipe.get(name);
    }

    @Override
    public Recipe getRecipe(int id) {
        return idToRecipe.get(id);
    }

    @Override
    public ArrayList<Ingredient> getIngredientsFromRecipe(String name) {
        return nameToRecipe.get(name).getIngredients();
    }

    @Override
    public ArrayList<Ingredient> getIngredientsFromRecipe(int id) {
        return idToRecipe.get(id).getIngredients();
    }

    @Override
    public Recipe getSuggestionRecipe(Recipe recipe) {
        return idToRecipe.get(1);
    }
}
