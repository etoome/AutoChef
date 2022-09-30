package ulb.infof307.g07.models.recipe;

import javafx.util.Pair;
import ulb.infof307.g07.controllers.components.search.Filter;
import ulb.infof307.g07.controllers.components.search.FilterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model used to make search in the recipe list, used a list of recipe in cache, and when a search is made return all
 * objects who match the filter, optimize to JavaFX, and doesn't need to call the DB each time
 */
public class RecipeSearch {

    public static final int WRONG_VALUE = -1;
    /** The cache recipeList which doesn't need to changed */
    private final ArrayList<Recipe> recipeList;

    public RecipeSearch(List<Recipe> recipeList) {
        if (recipeList == null) {
            throw new IllegalArgumentException("Can't set a null recipeList to RecipeSearch");
        }
        this.recipeList = new ArrayList<>(recipeList);
    }

    public static List<Filter> getFilter() {
        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter("Personnes", FilterType.INT, "%s personnes", "(\\d+) personnes?"));
        filters.add(new Filter("Temps", FilterType.INT, "%s min", "(\\d+) min(ute)?s?", "min"));
        List<String> types = new ArrayList<>();
        for (RecipeType type : RecipeType.values()) {
            types.add(type.toString());
        }
        filters.add(new Filter("Type", FilterType.CHOICEBOX, types));
        List<String> styles = new ArrayList<>();
        for (RecipeStyle style : RecipeStyle.values()) {
            styles.add(style.toString());
        }
        filters.add(new Filter("Style", FilterType.CHOICEBOX, styles));
        return filters;
    }

    public void addRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("Can't add a null recipe in RecipeSearch");
        }
        recipeList.add(recipe);
    }

    public void removeRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("Can't remove a null recipe in RecipeSearch");
        }
        recipeList.remove(recipe);
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    /**
     * Match the filter and the recipe, and return all objects to actually really display on the view
     *
     * @param values:
     *            filter value to match with each good recipe
     *
     * @return recipes to display, with all filters
     */
    public List<Recipe> search(List<Pair<String, String>> values) {
        if (values == null) {
            throw new IllegalArgumentException("Can't search recipes with a null value of filter");
        }
        List<Recipe> recipes = new ArrayList<>();
        for (Recipe recipe : recipeList) {
            boolean valid = true;
            for (Pair<String, String> value : values) {
                if (Objects.equals(value.getKey(), "")) {
                    Pattern pattern = Pattern.compile(value.getValue(), Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(recipe.getName());
                    valid = matcher.find();
                } else if (Objects.equals(value.getKey(), "Personnes")) {
                    valid = recipe.getNumberPeople() >= Integer.parseInt(value.getValue());
                } else if (Objects.equals(value.getKey(), "Temps")) {
                    valid = recipe.getTime() <= Integer.parseInt(value.getValue());
                } else if (Objects.equals(value.getKey(), "Type")) {
                    valid = recipe.getType() == RecipeType.getValue(value.getValue());
                } else if (Objects.equals(value.getKey(), "Style")) {
                    valid = recipe.getStyle() == RecipeStyle.getValue(value.getValue());
                }
                if (!valid)
                    break;
            }
            if (valid)
                recipes.add(recipe);
        }
        return recipes;
    }

    /**
     * Search for a list with the specifics recipe style
     *
     * @param recipeStyle
     *            the specific recipeStyle
     *
     * @return an arraylist with goods elements
     */
    public List<Recipe> searchRecipesStyles(RecipeStyle recipeStyle) {
        if (recipeStyle == null) {
            throw new IllegalArgumentException("Can't search recipeType with a null recipeStyle");
        }
        ArrayList<Recipe> recipes = new ArrayList<>();
        for (Recipe recipe : recipeList) {
            if (recipe.getStyle() == recipeStyle)
                recipes.add(recipe);
        }
        return recipes;
    }

}
