package ulb.infof307.g07.controllers.recipe;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import ulb.infof307.g07.controllers.components.PopupInformation;
import ulb.infof307.g07.controllers.components.search.SearchController;
import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.ErrorException;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.recipe.*;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;
import ulb.infof307.g07.utils.Colors;
import ulb.infof307.g07.utils.Delay;
import ulb.infof307.g07.utils.Now;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Main controller of the recipe section, display all recipes from database, use a search bar, handle creation & import
 * of recipes
 */
public class MainController implements Initializable {

    @FXML
    private TilePane fx_tile;
    @FXML
    private SVGPath fx_add;
    @FXML
    private SVGPath fx_import;
    @FXML
    private SearchController searchController;

    private RecipeSearch recipeSearch;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            recipeSearch = new RecipeSearch(Recipe.getDAO().getAllRecipes());
        } catch (RecipeException e) {
            new PopupInformation(PopupInformation.Styles.ERROR_POPUP, "");
            // Go back to an other tab, to force the user to try to reload recipe tab
            ExceptionManager.getInstance()
                    .handleException(new ErrorException("Problème avec le chargement des recettes", e));
            SceneManager.getInstance().setMainScene("shoppinglist/main.fxml");
        }
        searchController.set(RecipeSearch.getFilter(), this::search);
        for (Recipe recipe : recipeSearch.getRecipeList()) {
            loadRecipe(recipe);
        }
    }

    private void search(List<Pair<String, String>> values) {
        fx_tile.getChildren().clear();
        for (Recipe recipe : recipeSearch.search(values))
            loadRecipe(recipe);
    }

    private void reloadAllRecipes() {
        fx_tile.getChildren().clear();
        for (Recipe recipe : recipeSearch.getRecipeList()) {
            loadRecipe(recipe);
        }
    }

    /**
     * Load a recipe in a RecipeController and add it to the tile
     *
     * @param recipe:
     *            the recipe to load
     */
    private void loadRecipe(Recipe recipe) {
        fx_tile.getChildren().add(new RecipeItemController(recipe, this::edit));
    }

    /**
     * add a recipe to the recipes array and load the recipe, used when create a new recipe or import a recipe
     *
     * @param recipe:
     *            the recipe who was created
     */
    private void createRecipe(Recipe recipe) {
        if (recipe != null) { // Used to dynamically update the list of recipe, used for the creation & update
                              // of a
                              // recipe
            recipeSearch.addRecipe(recipe);
        }
        reloadAllRecipes();
    }

    /**
     * Create a new window to display the recipe in it, in view mod
     *
     * @param recipe:
     *            the recipe to display
     */
    private void edit(Recipe recipe) {
        var sceneManager = SceneManager.getInstance();
        sceneManager.createAuxiliaryStage("recipe/recipe_main.fxml", recipe.getName(), (scene, controller) -> {
            RecipeController viewController = controller.getController();
            viewController.start(recipe, false, this::deleteRecipe, this::createRecipe);
        });
    }

    /**
     * Create a new window to display the recipe in it, in edition mod
     *
     * @param recipe:
     *            the recipe to display in edition mod
     */
    private void editCreation(Recipe recipe) {
        var sceneManager = SceneManager.getInstance();
        sceneManager.createAuxiliaryStage("recipe/recipe_main.fxml", recipe.getName(), (scene, controller) -> {
            RecipeController viewController = controller.getController();
            viewController.start(recipe, true, this::deleteRecipe, this::createRecipe);
        });
    }

    private void deleteRecipe(Recipe recipe) {
        fx_tile.getChildren().remove(recipeSearch.getRecipeList().indexOf(recipe));
        recipeSearch.removeRecipe(recipe);
    }

    /**
     * Import a recipe from a JSON file, using RecipeJson model, and add it to the database, and open a display view of
     * the recipe
     */
    @FXML
    private void importJsonRecipe() {
        fx_import.setFill(Colors.DISABLE.getColor());
        Delay.wait(500, () -> {
            if (fx_import.getFill() != Colors.ERROR.getColor()) {
                fx_import.setFill(Colors.ENABLE.getColor());
            }
        });

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        var sceneManager = SceneManager.getInstance();
        File file = fileChooser.showOpenDialog(sceneManager.getMainStage());
        try {
            Recipe importedRecipe = RecipeJSON.getInstance().importRecipe(file);
            assert importedRecipe != null;
            editCreation(importedRecipe);
        } catch (RecipeException e) {
            fx_import.setFill(Colors.ERROR.getColor());
            ExceptionManager.getInstance().handleException(
                    new ErrorException("Problème lors de l'importation de la recette sous format JSON", e));
        }
    }

    /**
     * Action done when click on the add button in the view, create a new recipe, and display the ViewController via the
     * RecipeController
     */
    @FXML
    private void addNewRecipe() {
        String name = new Now().toString();
        Recipe recipe = null;
        try {
            recipe = new Recipe(name);
        } catch (RecipeException e) {
            ExceptionManager.getInstance()
                    .handleException(new ErrorException("Problème lors de la création d'une recette", e));
        }
        assert recipe != null;
        editCreation(recipe);
    }

}
