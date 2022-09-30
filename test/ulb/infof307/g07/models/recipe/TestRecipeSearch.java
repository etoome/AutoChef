package ulb.infof307.g07.models.recipe;

import javafx.util.Pair;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g07.controllers.components.search.Filter;
import ulb.infof307.g07.controllers.components.search.FilterType;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestRecipeSearch {

    RecipeSearch testRecipeSearch;
    Recipe recipe1 = new Recipe("Burger", 1, new ArrayList<>(), new JSONObject(), RecipeStyle.MEAT, RecipeType.MEAL,
            10);
    Recipe recipe2 = new Recipe("BurgerVegan", 1, new ArrayList<>(), new JSONObject(), RecipeStyle.VEGAN,
            RecipeType.MEAL, 20);
    Recipe recipe3 = new Recipe("Cheeseburger", 2, new ArrayList<>(), new JSONObject(), RecipeStyle.MEAT,
            RecipeType.MEAL, 10);
    Recipe recipe4 = new Recipe("Salade", 4, new ArrayList<>(), new JSONObject(), RecipeStyle.VEGAN, RecipeType.ENTREE,
            5);

    TestRecipeSearch() throws RecipeException {
    }

    @BeforeEach
    void resetTestData() {
        testRecipeSearch = new RecipeSearch(Arrays.asList(recipe1, recipe2, recipe3, recipe4));
    }

    @Test
    void testRecipeSearchConstructorNullRecipeListShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new RecipeSearch(null));
    }

    @Test
    void testRecipeSearchCreateSuccessfully() {
        assertEquals(testRecipeSearch.getRecipeList(), Arrays.asList(recipe1, recipe2, recipe3, recipe4));
    }

    @Test
    void testAddRecipeWithNullRecipeShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testRecipeSearch.addRecipe(null));
    }

    @Test
    void testAddRecipeCorrect() {
        testRecipeSearch.addRecipe(recipe3);
        assertEquals(testRecipeSearch.getRecipeList(), Arrays.asList(recipe1, recipe2, recipe3, recipe4, recipe3));
    }

    @Test
    void testRemoveRecipeWithNullRecipeShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testRecipeSearch.removeRecipe(null));
    }

    @Test
    void testRemoveRecipeCorrect() {
        testRecipeSearch.removeRecipe(recipe1);
        assertEquals(testRecipeSearch.getRecipeList(), Arrays.asList(recipe2, recipe3, recipe4));
    }

    @Test
    void testSearchWithNullFilterShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testRecipeSearch.search(null));
    }

    @Test
    void testSearchSentenceCorrect() {
        List<Pair<String, String>> values = new ArrayList<>();
        values.add(new Pair<>("", "burger"));
        assertEquals(testRecipeSearch.search(values), Arrays.asList(recipe1, recipe2, recipe3));
        values.set(0, new Pair<>("", "Pizza"));
        assertEquals(testRecipeSearch.search(values), List.of());
    }

    @Test
    void testSearchPeopleCorrect() {
        List<Pair<String, String>> values = new ArrayList<>();
        values.add(new Pair<>("Personnes", "2"));
        assertEquals(testRecipeSearch.search(values), Arrays.asList(recipe3, recipe4));
        values.set(0, new Pair<>("Personnes", "5"));
        assertEquals(testRecipeSearch.search(values), List.of());
    }

    @Test
    void testSearchTimeCorrect() {
        List<Pair<String, String>> values = new ArrayList<>();
        values.add(new Pair<>("Temps", "19"));
        assertEquals(testRecipeSearch.search(values), Arrays.asList(recipe1, recipe3, recipe4));
        values.set(0, new Pair<>("Temps", "5"));
        assertEquals(testRecipeSearch.search(values), List.of(recipe4));
    }

    @Test
    void testSearchTypeCorrect() {
        List<Pair<String, String>> values = new ArrayList<>();
        values.add(new Pair<>("Type", "Plat"));
        assertEquals(testRecipeSearch.search(values), Arrays.asList(recipe1, recipe2, recipe3));
        values.set(0, new Pair<>("Type", "Dessert"));
        assertEquals(testRecipeSearch.search(values), List.of());
    }

    @Test
    void testSearchStyleCorrect() {
        List<Pair<String, String>> values = new ArrayList<>();
        values.add(new Pair<>("Style", "Vegan"));
        assertEquals(testRecipeSearch.search(values), Arrays.asList(recipe2, recipe4));
        values.set(0, new Pair<>("Style", "Poisson"));
        assertEquals(testRecipeSearch.search(values), List.of());
    }

    @Test
    void testSearchRecipesTypeWithNullRecipeTypeShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testRecipeSearch.searchRecipesStyles(null));
    }

    @Test
    void testSearchRecipesTypeCorrect() {
        assertEquals(testRecipeSearch.searchRecipesStyles(RecipeStyle.VEGAN), Arrays.asList(recipe2, recipe4));
        assertEquals(testRecipeSearch.searchRecipesStyles(RecipeStyle.MEAT), Arrays.asList(recipe1, recipe3));
        assertEquals(testRecipeSearch.searchRecipesStyles(RecipeStyle.FISH), List.of());
    }

    @Test
    void testFilterReturnCorrect() {//
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
        assertEquals(RecipeSearch.getFilter().size(), filters.size());
    }

}