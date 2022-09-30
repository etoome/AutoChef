package ulb.infof307.g07.models.agenda;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g07.models.agenda.exceptions.AgendaException;
import ulb.infof307.g07.models.recipe.Recipe;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;
import ulb.infof307.g07.models.recipe.RecipeStyle;
import ulb.infof307.g07.models.recipe.RecipeType;
import ulb.infof307.g07.utils.Config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestAgenda {
    Agenda testAgenda;
    Recipe testRecipe = new Recipe("Poulet Tikka", 4, new ArrayList<>(), new JSONObject(), RecipeStyle.MEAT,
            RecipeType.MEAL, 45);
    Menu testMenu = new Menu("Menu");
    LocalDate testBeginDate = LocalDate.of(2022, 1, 1);
    LocalDate testEndDate = LocalDate.of(2022, 1, 7);
    LocalDate testBadDateEarly = LocalDate.of(1, 1, 1);
    LocalDate testBadDateLate = LocalDate.of(3000, 1, 1);

    public TestAgenda() throws RecipeException {
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
    public void resetAgenda() throws AgendaException {
        testAgenda = new Agenda("AgendaTest", testBeginDate, testEndDate);
    }

    @Test
    public void testAgendaCreatedSuccessfully() {
        assertEquals(testAgenda.getName(), "AgendaTest");
        assertEquals(testAgenda.getDateBegin(), LocalDate.of(2022, 1, 1));
        assertEquals(testAgenda.getDateEnd(), LocalDate.of(2022, 1, 7));

        HashMap<LocalDate, ArrayList<Menu>> attemptedAgenda = new HashMap<>();
        for (int i = 1; i <= 7; i++)
            attemptedAgenda.put(LocalDate.of(2022, 1, i), new ArrayList<>());
        assertEquals(testAgenda.getAllMenus(), attemptedAgenda);
    }

    @Test
    public void testAgendaConstructorNullNameShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Agenda(null, testBeginDate, testEndDate));
    }

    @Test
    public void testAgendaConstructorNullBeginDateShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Agenda("some name", null, testEndDate));
    }

    @Test
    public void testAgendaConstructorNullEndDateShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Agenda("some name", testBeginDate, null));
    }

    @Test
    public void testAgendaConstructorInvalidDateShouldThrow() {
        assertThrows(AgendaException.class, () -> new Agenda("some name", testEndDate, testBeginDate));
    }

    @Test
    public void testAgendaConstructionWithRecipesWorks() throws AgendaException {
        new Agenda("some name", testBeginDate, testEndDate, 1,
                Map.of(RecipeStyle.MEAT, 1, RecipeStyle.VEGGIE, 0, RecipeStyle.FISH, 0, RecipeStyle.VEGAN, 0));
    }

    @Test
    public void testAgendaConstructionWithRecipesNullNameShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Agenda(null, testBeginDate, testEndDate, 1,
                Map.of(RecipeStyle.MEAT, 1, RecipeStyle.VEGGIE, 0, RecipeStyle.FISH, 0, RecipeStyle.VEGAN, 0)));
    }

    @Test
    public void testAgendaConstructionWithRecipesNullBeginDateShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Agenda("some name", null, testEndDate, 1,
                Map.of(RecipeStyle.MEAT, 1, RecipeStyle.VEGGIE, 0, RecipeStyle.FISH, 0, RecipeStyle.VEGAN, 0)));
    }

    @Test
    public void testAgendaConstructionWithRecipesNullEndDateShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Agenda("some name", testBeginDate, null, 1,
                Map.of(RecipeStyle.MEAT, 1, RecipeStyle.VEGGIE, 0, RecipeStyle.FISH, 0, RecipeStyle.VEGAN, 0)));
    }

    @Test
    public void testAgendaConstructionWithRecipesNullRecipeStylesCountShouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> new Agenda("some name", testBeginDate, testEndDate, 1, null));
    }

    @Test
    public void testAgendaConstructionWithRecipesIncompleteStyleMapShouldThrow() {
        assertThrows(AgendaException.class, () -> new Agenda("some name", testBeginDate, testEndDate, 1,
                Map.of(RecipeStyle.MEAT, 1, RecipeStyle.VEGGIE, 0, RecipeStyle.FISH, 0
                // missing VEGAN entry
                )));
    }

    @Test
    public void testSetNameWithNullNameShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testAgenda.setName(null));
    }

    @Test
    public void testAgendaAddEmptyMenusForDayWorks() throws AgendaException {
        testAgenda.addMenuForDay(testBeginDate, testMenu);
        ArrayList<Menu> dateMenu = new ArrayList<>();
        dateMenu.add(testMenu);
        assertEquals(dateMenu, testAgenda.getAllMenusForDay(testBeginDate));
    }

    @Test
    public void testAgendaAddMenusForDayWorks() throws AgendaException {
        ArrayList<Menu> dateMenu = new ArrayList<>();
        dateMenu.add(testMenu);
        dateMenu.add(testMenu);
        testAgenda.addMenusForDay(testBeginDate, dateMenu);
        assertEquals(dateMenu, testAgenda.getAllMenusForDay(testBeginDate));
    }

    @Test
    public void testAddMenusForDayWithNullDateShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testAgenda.addMenusForDay(null, List.of(testMenu)));
    }

    @Test
    public void testAddMenusForDayWithNullMenusShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testAgenda.addMenusForDay(testBeginDate, null));
    }

    @Test
    public void testAddMenusForDayInvalidDateShouldThrow() {
        assertThrows(AgendaException.class, () -> testAgenda.addMenusForDay(testBadDateLate, List.of()));
    }

    @Test
    public void testAddMenuForDayWithNullDateShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testAgenda.addMenuForDay(null, testMenu));
    }

    @Test
    public void testAddMenuForDayWithNullMenusShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testAgenda.addMenuForDay(testBeginDate, null));
    }

    @Test
    public void testAddMenuForDayWithInvalidDateShouldThrow() {
        assertThrows(AgendaException.class, () -> testAgenda.addMenuForDay(testBadDateLate, testMenu));
    }

    @Test
    public void testRemoveMenuDayWorks() throws AgendaException {
        testAgenda.addMenuForDay(testBeginDate, testMenu);
        testAgenda.removeMenuForDay(testBeginDate, testMenu);
        ArrayList<Menu> dateMenu = new ArrayList<>();
        assertEquals(dateMenu, testAgenda.getAllMenusForDay(testBeginDate));
    }

    @Test
    public void testRemoveMenuForDay() throws AgendaException {
        testAgenda.addMenuForDay(testBeginDate, testMenu);
        assertThrows(AgendaException.class, () -> testAgenda.removeMenuForDay(testEndDate.plusYears(1), testMenu));
    }

    @Test
    public void testAddRecipeToMenuWorks() throws AgendaException {
        testAgenda.addMenuForDay(testBeginDate, testMenu);
        testAgenda.addRecipeToMenu(testBeginDate, testMenu, testRecipe);
        assertEquals(testMenu, testAgenda.getAllMenusForDay(testBeginDate).get(0));
    }

    @Test
    public void testRemoveRecipeFromMenuWorks() throws AgendaException {
        testAgenda.addMenuForDay(testBeginDate, testMenu);
        testAgenda.addRecipeToMenu(testBeginDate, testMenu, testRecipe);
        testAgenda.removeRecipeFromMenu(testBeginDate, testMenu, testRecipe);
        assertEquals(new ArrayList<Recipe>(), testAgenda.getAllMenusForDay(testBeginDate).get(0).getAllRecipes());
    }

    @Test
    public void testRemoveRecipeFromMenuThrowsWithInvalidDate() {
        assertThrows(AgendaException.class,
                () -> testAgenda.removeRecipeFromMenu(testBadDateEarly, testMenu, testRecipe));
    }

    @Test
    public void testRemoveRecipeFromMenuThrowsWithInvalidMenu() {
        assertThrows(AgendaException.class, () -> testAgenda.removeRecipeFromMenu(testBeginDate, testMenu, testRecipe));
    }

    @Test
    public void testRemoveRecipeFromMenuDoesNotThrowWithInvalidRecipe() throws AgendaException {
        testAgenda.addMenuForDay(testBeginDate, testMenu);
        assertDoesNotThrow(() -> testAgenda.removeRecipeFromMenu(testBeginDate, testMenu, testRecipe));
    }

    @Test
    public void testRemoveMenuForDayWithNullDateShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testAgenda.removeMenuForDay(null, testMenu));
    }

    @Test
    public void testRemoveMenuForDayWithNullMenuShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testAgenda.removeMenuForDay(testBeginDate, null));
    }

    @Test
    void testAgendaInsertCorrect() {
        testMenu.addRecipe(testRecipe);
        assertDoesNotThrow(() -> testAgenda.addMenuForDay(LocalDate.of(2022, 1, 5), testMenu));
        ArrayList<Menu> attemptedMenu = new ArrayList<>();
        attemptedMenu.add(testMenu);
        assertEquals(testAgenda.getAllMenusForDay(LocalDate.of(2022, 1, 5)), attemptedMenu);
    }

    @Test
    public void testSetNameNullShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testAgenda.setName(null));
    }

    @Test
    public void testSetMenuWithIncorrectDates() {
        HashMap<LocalDate, ArrayList<Menu>> menus = new HashMap<>();
        ArrayList<Menu> menu = new ArrayList<>();
        menu.add(new Menu("some menu name"));
        menus.put(LocalDate.of(1000, 1, 1), menu);
        assertThrows(AgendaException.class, () -> testAgenda.setMenus(menus));
        menus.clear();
        menus.put(LocalDate.of(3000, 1, 1), menu);
        assertThrows(AgendaException.class, () -> testAgenda.setMenus(menus));
    }

    @Test
    public void testSetMenusWithNullMenusShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testAgenda.setMenus(null));
    }

    @Test
    public void testAddRecipeToMenuWithInvalidDateShouldThrow() {
        assertThrows(AgendaException.class,
                () -> testAgenda.addRecipeToMenu(LocalDate.of(300, 1, 1), testMenu, testRecipe));
    }

    @Test
    public void testAddRecipeToMenuWithInvalidMenuShouldThrow() {
        assertThrows(AgendaException.class,
                () -> testAgenda.addRecipeToMenu(LocalDate.of(2022, 1, 1), testMenu, testRecipe));
    }

    @Test
    public void testAddRecipeToMenuWithNullDateShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testAgenda.addRecipeToMenu(null, testMenu, testRecipe));
    }

    @Test
    public void testAddRecipeToMenuWithNullMenuShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testAgenda.addRecipeToMenu(testBeginDate, null, testRecipe));
    }

    @Test
    public void testAddRecipeToMenuWithNullRecipeShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testAgenda.addRecipeToMenu(testBeginDate, testMenu, null));
    }
}
