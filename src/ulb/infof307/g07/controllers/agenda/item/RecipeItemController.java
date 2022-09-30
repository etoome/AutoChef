package ulb.infof307.g07.controllers.agenda.item;

import javafx.fxml.FXML;
import ulb.infof307.g07.models.recipe.Recipe;

/**
 * The controller of the recipe view, the display of the recipe in the agenda, only use a Recipe object
 */
public final class RecipeItemController extends RecipeAgenda {

    public RecipeItemController(Recipe recipe) {
        super(recipe, "agenda/recipe_item.fxml");
    }

    @FXML
    private void handleDragDetection() {
        handleDrag();
    }
}
