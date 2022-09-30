package ulb.infof307.g07.models.agenda;

import ulb.infof307.g07.database.dao.DAO;
import ulb.infof307.g07.database.dao.agenda.AgendaDAO;
import ulb.infof307.g07.models.agenda.exceptions.AgendaException;
import ulb.infof307.g07.models.agenda.exceptions.DateRangeException;
import ulb.infof307.g07.models.recipe.*;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;
import ulb.infof307.g07.models.shoppinglist.exceptions.ShoppingListException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

/**
 * The agenda object who contains all recipes and agenda information, beginning date, end date, recipes, menus for a day
 * you can have more than one recipe for a day, use DateRange class to work
 */
public final class Agenda {

    private String name;
    private final DateRange dates;
    private HashMap<LocalDate, ArrayList<Menu>> menus;

    /**
     * Handle the creation of an empty agenda
     *
     * @param name:
     *            the name of the agenda
     * @param beginning:
     *            the beginning date of the agenda
     * @param end:
     *            the end date of the agenda
     */
    public Agenda(String name, LocalDate beginning, LocalDate end) throws AgendaException {
        if (name == null) {
            throw new IllegalArgumentException(AgendaException.ERROR_MSG_NAME_NULL);
        }
        if (beginning == null) {
            throw new IllegalArgumentException("Can't create an Agenda with a null begining date");
        }
        if (end == null) {
            throw new IllegalArgumentException("Can't create an Agenda with a null ending date");
        }
        this.name = name;
        try {
            this.dates = new DateRange(beginning, end);
        } catch (DateRangeException e) {
            throw new AgendaException(AgendaException.ERROR_MSG_INVALID_DATE, e);
        }
        menus = new HashMap<>();
        for (LocalDate day : dates.getDays()) {
            menus.put(day, new ArrayList<>());
        }
    }

    /**
     * Handle the creation of an agenda with the automatic generation of a list of recipe for each day
     *
     * @param name:
     *            the name of the agenda
     * @param beginning:
     *            the beginning date of the agenda
     * @param end:
     *            the end date of the agenda
     * @param recipePerDay:
     *            the number of recipe per day to generate
     * @param nbStyles:
     *            the number of recipe with each style (refer to RecipeStyle)
     */
    public Agenda(String name, LocalDate beginning, LocalDate end, int recipePerDay, Map<RecipeStyle, Integer> nbStyles)
            throws AgendaException {
        this(name, beginning, end);
        try {
            generateAgenda(recipePerDay, nbStyles);
        } catch (AgendaException e) {
            throw new AgendaException("Error when generating agenda ", e);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException(AgendaException.ERROR_MSG_NAME_NULL);
        this.name = name;
    }

    public List<LocalDate> getDays() {
        return dates.getDays();
    }

    public List<LocalDate> getDaysWeek() {
        return dates.getDaysWeek();
    }

    public int getLength() {
        return dates.getLength();
    }

    public LocalDate getDateBegin() {
        return dates.getBeginningDate();
    }

    public LocalDate getDateEnd() {
        return dates.getEndDate();
    }

    public List<Menu> getAllMenusForDay(LocalDate date) {
        return menus.get(date);
    }

    public Map<LocalDate, ArrayList<Menu>> getAllMenus() {
        return menus;
    }

    public static AgendaDAO getDAO() {
        return DAO.getInstance().getAgendaDAO();
    }

    /**
     * Set all menus for the entire agenda, for each day
     *
     * @param menus:
     *            Map of menus with local, used to set all everything
     *
     * @throws AgendaException:
     *             error handled above
     */
    public void setMenus(Map<LocalDate, ArrayList<Menu>> menus) throws AgendaException {
        if (menus == null) {
            throw new IllegalArgumentException("Can't set null menus in the agenda.");
        }
        for (LocalDate key : menus.keySet()) {
            if (key.isBefore(dates.getBeginningDate()) || key.isAfter(dates.getEndDate())) {
                throw new AgendaException(AgendaException.ERROR_MSG_INVALID_MENU_DATE);
            }
        }
        this.menus = (HashMap<LocalDate, ArrayList<Menu>>) menus;
    }

    /**
     * Add a list menu to a day
     *
     * @param date:
     *            the date to add menus
     * @param menus:
     *            the array list of menu objects
     */
    public void addMenusForDay(LocalDate date, List<Menu> menus) throws AgendaException {
        if (menus == null) {
            throw new IllegalArgumentException("Can't set null menus in the agenda for a day.");
        }
        if (date == null) {
            throw new IllegalArgumentException("Can't set null date for menus of the day.");
        }
        try {
            dates.assertDateIsInDateRange(date);
            if (!dates.getEndDate().isBefore(date)) { // Used when change date of an agenda before the actual end
                for (Menu menu : menus) {
                    addMenuForDay(date, menu);
                }
            }
        } catch (DateRangeException e) {
            throw new AgendaException(AgendaException.ERROR_MSG_INVALID_MENU_DATE, e);
        }
    }

    /**
     * Add a single menu to day
     *
     * @param date:
     *            the date of the day
     * @param menu:
     *            the menu to add
     */
    public void addMenuForDay(LocalDate date, Menu menu) throws AgendaException {
        if (menu == null) {
            throw new IllegalArgumentException("Can't set null menus in the agenda for a day.");
        }
        if (date == null) {
            throw new IllegalArgumentException("Can't set null date for menus of the day.");
        }
        try {
            dates.assertDateIsInDateRange(date);
            this.menus.get(date).add(menu);
        } catch (DateRangeException e) {
            throw new AgendaException(AgendaException.ERROR_MSG_INVALID_MENU_DATE, e);
        }
    }

    /**
     * Remove a single menu in a day
     *
     * @param date:
     *            the date of the day
     * @param menu:
     *            the menu to add
     */
    public void removeMenuForDay(LocalDate date, Menu menu) throws AgendaException {
        if (date == null) {
            throw new IllegalArgumentException("Can't remove a null date");
        }
        if (menu == null) {
            throw new IllegalArgumentException("Can't remove a null menu");
        }
        try {
            dates.assertDateIsInDateRange(date);
            menus.get(date).remove(menu);
        } catch (DateRangeException e) {
            throw new AgendaException("Can't remove a menu with an incorrect date", e);
        }
    }

    /**
     * Add a recipe to a specific menu in a day of the agenda
     */
    public void addRecipeToMenu(LocalDate date, Menu menu, Recipe recipe) throws AgendaException {
        if (date == null) {
            throw new IllegalArgumentException("Can't add recipe with null date");
        }
        if (menu == null) {
            throw new IllegalArgumentException("Can't add null menu");
        }
        if (recipe == null) {
            throw new IllegalArgumentException("Can't add null recipe to menu");
        }
        try {
            dates.assertDateIsInDateRange(date);
            if (menus.get(date).contains(menu)) {
                menus.get(date).get(menus.get(date).indexOf(menu)).addRecipe(recipe);
            } else {
                throw new AgendaException("Can't add a recipe to a menu that does not belong to the agenda");
            }
        } catch (DateRangeException e) {
            throw new AgendaException(AgendaException.ERROR_MSG_INVALID_ACTION_DATE, e);
        }
    }

    /**
     * Remove a recipe from a specific menu in an agenda
     */
    public void removeRecipeFromMenu(LocalDate date, Menu menu, Recipe recipe) throws AgendaException {
        try {
            dates.assertDateIsInDateRange(date);
            if (menus.get(date).contains(menu)) {
                menus.get(date).get(menus.get(date).indexOf(menu)).deleteRecipe(recipe);
            } else {
                throw new AgendaException(
                        "Can't remove a recipe from a menu if the menu does not belong to the agenda");
            }
        } catch (DateRangeException e) {
            throw new AgendaException(AgendaException.ERROR_MSG_INVALID_ACTION_DATE, e);
        }
    }

    /**
     * Generate an agenda of menus with a number of menu per day and a number a recipe style in total
     *
     * @param menuPerDay:
     *            number of menus per day
     * @param typeMenus:
     *            all recipes type needed and number of each
     */
    public void generateAgenda(int menuPerDay, Map<RecipeStyle, Integer> typeMenus) throws AgendaException {
        if (typeMenus == null) {
            throw new IllegalArgumentException("Mapping of wanted Menu styles can't be null");
        }
        if (typeMenus.entrySet().size() != RecipeStyle.values().length) {
            throw new AgendaException("Mapping for menu types is incomplete");
        }

        // Never remove this element, use a cache variable, except bad type recipe
        ArrayList<Recipe> recipes;
        try {
            recipes = Recipe.getDAO().getAllRecipes();
        } catch (RecipeException e) {
            throw new AgendaException("Error when recovering recipes in the database", e);
        }
        // Remove every recipe who can't be used to be generated in a menu
        recipes.removeIf(recipe -> recipe.getType() == RecipeType.ENTREE || recipe.getType() == RecipeType.DESSERT
                || recipe.getType() == RecipeType.DRINK);
        // Actual list who used settings from the user
        ArrayList<Recipe> allRecipes = getListNeededRecipe(recipes, typeMenus);
        // If we need more recipes, it completes with random recipes, trying to avoid adding duplicated recipes
        Random rng;
        try {
            rng = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new AgendaException(e.getMessage());
        }
        if (dates.getLength() * menuPerDay > allRecipes.size()) {
            // create a list with elements who aren't needed to avoid duplicated recipes
            ArrayList<Recipe> tempRecipes = new ArrayList<>(recipes);
            tempRecipes.removeAll(allRecipes);
            for (int i = allRecipes.size(); i < dates.getLength() * menuPerDay; i++) {
                if (tempRecipes.isEmpty())
                    tempRecipes = new ArrayList<>(recipes);
                allRecipes.add(tempRecipes.remove(rng.nextInt(tempRecipes.size())));
            }
        }
        for (LocalDate day : dates.getDays()) {
            for (int i = 0; i < menuPerDay; i++) {
                Recipe tempRecipe = allRecipes.remove(rng.nextInt(allRecipes.size()));
                addMenuForDay(day, new Menu(tempRecipe.getName(), tempRecipe));
            }
        }
    }

    /**
     * Methods used to get a Arraylist containing all recipes who're ok with the setting of the user
     *
     * @param recipes:
     *            list of all recipes
     * @param typeMenus:
     *            setting of the user, like how much style of each recipe he want
     *
     * @return Arraylist containing good's elements
     */
    private ArrayList<Recipe> getListNeededRecipe(ArrayList<Recipe> recipes, Map<RecipeStyle, Integer> typeMenus) {
        RecipeSearch recipeInformation = new RecipeSearch(recipes);
        ArrayList<Recipe> neededRecipe = new ArrayList<>();
        for (Map.Entry<RecipeStyle, Integer> typeMenu : typeMenus.entrySet()) {
            ArrayList<Recipe> recipeStyleList = (ArrayList<Recipe>) recipeInformation
                    .searchRecipesStyles(typeMenu.getKey());
            if (!recipeStyleList.isEmpty()) {
                for (int i = 0; i < typeMenu.getValue(); i++) {
                    if (recipeStyleList.isEmpty())
                        recipeStyleList = (ArrayList<Recipe>) recipeInformation.searchRecipesStyles(typeMenu.getKey());
                    neededRecipe.add(recipeStyleList.remove((int) (Math.random() * recipeStyleList.size())));
                }
            }
        }
        return neededRecipe;
    }

    /**
     * This method create a shopping list from this actual agenda, using the name of the agenda as name for the shopping
     * list
     */
    public static void createShoppingList(Agenda agenda) throws AgendaException {
        String name = agenda.getName();
        try {
            if (ShoppingList.getDAO().shoppingListExist(name))
                name += "_" + LocalDate.now() + System.currentTimeMillis();
        } catch (ShoppingListException e) {
            throw new AgendaException("Error when checking if a shopping list already exists");
        }
        agenda.setName(name);
        try {
            var shoppingList = Agenda.getDAO().generateShoppingList(agenda);
            ShoppingList.getDAO().createShoppingList(shoppingList.getName());
            ShoppingList.getDAO().save(shoppingList);
        } catch (ShoppingListException e) {
            throw new AgendaException("Error when creating an shopping list with an agenda", e);
        }
    }

}
