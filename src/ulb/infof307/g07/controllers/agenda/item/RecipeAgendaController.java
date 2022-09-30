package ulb.infof307.g07.controllers.agenda.item;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import ulb.infof307.g07.controllers.recipe.RecipeController;
import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.ErrorException;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.recipe.Recipe;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;

/**
 * The controller of the recipe view, the display of the recipe in the agenda, only use a Recipe object
 */
public final class RecipeAgendaController extends RecipeAgenda {

    @FXML
    private HBox fx_trashButton;
    @FXML
    private HBox fx_reloadButton;

    private boolean isShowOnly = false;

    public static final int RECIPE_ITEM_HEIGHT = 92;

    public RecipeAgendaController(Recipe recipe) {
        super(recipe, "agenda/recipe_agenda.fxml");
    }

    public void showOnly() {
        fx_trashButton.setOpacity(0);
        fx_reloadButton.setOpacity(0);
        isShowOnly = true;
    }

    @FXML
    private void goToRecipe() {
        var sceneManager = SceneManager.getInstance();
        sceneManager.createAuxiliaryStage("recipe/recipe_main.fxml", recipe.getName(), (scene, controller) -> {
            RecipeController recipeController = controller.getController();
            recipeController.start(recipe);
        });
    }

    @FXML
    private void onReloadClick() {
        try {
            this.recipe = Recipe.getDAO().getSuggestionRecipe(this.recipe);
        } catch (RecipeException e) {
            ExceptionManager.getInstance()
                    .handleException(new ErrorException("Problème lors de la suggestion de recette", e));
        }
        setRecipe();
    }

    @FXML
    private void onDeleteClick() {
        this.setDisable(!isShowOnly);
    }

    @FXML
    private void handleDragDetection() {
        handleDrag();
        this.setDisable(!isShowOnly);
    }
}
