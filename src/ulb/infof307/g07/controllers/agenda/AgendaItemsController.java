package ulb.infof307.g07.controllers.agenda;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import ulb.infof307.g07.controllers.agenda.item.MenuItemController;
import ulb.infof307.g07.controllers.agenda.item.RecipeItemController;
import ulb.infof307.g07.controllers.components.search.Filter;
import ulb.infof307.g07.controllers.components.search.FilterType;
import ulb.infof307.g07.controllers.components.search.SearchController;
import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.CriticalException;
import ulb.infof307.g07.managers.exception.types.ErrorException;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.agenda.Menu;
import ulb.infof307.g07.models.agenda.exceptions.MenuException;
import ulb.infof307.g07.models.recipe.*;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;

import java.util.List;
import java.util.Objects;

/**
 * The controller of the recipe view, the display of the recipe in the agenda, only use a Recipe object
 */
public class AgendaItemsController extends VBox {

    @FXML
    private VBox fx_recipeList;
    @FXML
    private SearchController searchController;

    private final AgendaZoneController agendaZone;
    // Cache recipe to made searches and don't need to make a database request for each search
    private RecipeSearch recipeSearch;

    public AgendaItemsController(AgendaZoneController agendaZone) {
        this.agendaZone = agendaZone;
        SceneManager.getInstance().loadFxmlNode(this, "agenda/agenda_items.fxml");

        try {
            recipeSearch = new RecipeSearch(Recipe.getDAO().getAllRecipes());
        } catch (RecipeException e) {
            ExceptionManager.getInstance()
                    .handleException(new CriticalException("Il y a eu un problème avec votre liste de recette", e));
        }
        initSearchBar();
        loadRecipe(recipeSearch.getRecipeList());
    }

    private void loadRecipe(List<Recipe> recipes) {
        fx_recipeList.getChildren().clear();
        // Initialize the recipe list
        for (Recipe recipe : recipes) {
            fx_recipeList.getChildren().add(new RecipeItemController(recipe));
        }
    }

    private void initSearchBar() {
        List<Filter> filters = RecipeSearch.getFilter();
        filters.add(new Filter("Menu", FilterType.CHECKBOX, " menu"));
        searchController.set(filters, this::search);
    }

    private void search(List<Pair<String, String>> values) {
        boolean isMenu = false;
        for (Pair<String, String> value : values) {
            if (Objects.equals(value.getKey(), "Menu"))
                isMenu = true;
        }
        if (isMenu) {
            fx_recipeList.getChildren().clear();
            try {
                for (Menu menu : Objects.requireNonNull(Menu.getDAO().getAllMenus())) {
                    fx_recipeList.getChildren().add(new MenuItemController(menu, this::deleteMenuItem));
                }
            } catch (MenuException e) {
                ExceptionManager.getInstance()
                        .handleException(new ErrorException("Problème lors de la récupération des menus", e));
            }
        } else
            loadRecipe(recipeSearch.search(values));
    }

    public void deleteMenuItem(MenuItemController mac) {
        try {
            Menu.getDAO().deleteMenu(mac.getMenu());
            agendaZone.removeMenu(mac.getMenu());
            fx_recipeList.getChildren().remove(mac);
        } catch (MenuException e) {
            ExceptionManager.getInstance()
                    .handleException(new ErrorException("Problème lors de la suppression de la recette", e));
        }
    }
}
