package ulb.infof307.g07.controllers.recipe;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ulb.infof307.g07.models.recipe.*;

/**
 * Controller of the recipe display in View mod, used by the RecipeController, who create this.
 */
public final class ViewController extends RecipeBaseController {

    @FXML
    private Label fx_people;
    @FXML
    private Label fx_time;
    @FXML
    private Label fx_type;
    @FXML
    private Label fx_style;

    private final OnAction.OnSwitchViewEdit switchViewEdit;

    public ViewController(Recipe recipe, OnAction.OnSwitchViewEdit switchViewEdit) {
        super(recipe, "recipe/view.fxml", true);
        this.switchViewEdit = switchViewEdit;
        render();
    }

    /**
     * Render all JavaFX components setting value from the recipe object
     */
    private void render() {
        fx_people.setText(String.valueOf(recipe.getNumberPeople()));
        fx_time.setText(String.valueOf(recipe.getTime()));
        fx_type.setText(recipe.getType().toString());
        fx_style.setText(recipe.getStyle().toString());
        renderIngredientsInstructions();
    }

    @FXML
    private void onEditClick() {
        switchViewEdit.execute(recipe);
    }

}
