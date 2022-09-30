package ulb.infof307.g07.controllers.agenda.item;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import ulb.infof307.g07.App;
import ulb.infof307.g07.controllers.agenda.RecipeStyleView;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.recipe.Recipe;

import java.util.Objects;

/**
 * Mother class for recipe controllers in the agenda
 */
public abstract class RecipeAgenda extends AnchorPane {

    protected Recipe recipe;
    @FXML
    private Label fx_recipeName;
    @FXML
    private Label fx_recipeType;
    @FXML
    private ImageView fx_recipeStyle;

    // Didn't find a better way, you could try a better workaround to drag&drop, but
    // actually I give up
    private static Recipe draggedRecipe;

    protected RecipeAgenda(Recipe recipe, String fxmlSource) {
        this.recipe = recipe;
        SceneManager.getInstance().loadFxmlNode(this, fxmlSource);
        setRecipe();
    }

    protected void setRecipe() {
        fx_recipeName.setText(this.recipe.getName());
        fx_recipeType.setText(this.recipe.getType().toString());
        fx_recipeStyle.setImage(new Image(
                Objects.requireNonNull(getClass().getClassLoader()
                        .getResource(RecipeStyleView.getInstance().getImgPath(this.recipe.getStyle()))).toString(),
                true));
        this.setStyle(this.getStyle() + "-fx-border-color: " + RecipeStyleView.getInstance().getColor(recipe.getStyle())
                + ";");
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public static Recipe getDraggedRecipe() {
        return draggedRecipe;
    }

    public static void setDraggedRecipe(Recipe recipe) {
        draggedRecipe = recipe;
    }

    /**
     * Handle the drag of a recipe, save the recipe in static object draggedRecipe
     */
    protected void handleDrag() {
        Dragboard db = this.startDragAndDrop(TransferMode.ANY);
        ClipboardContent cb = new ClipboardContent();
        cb.putImage(new Image(String.valueOf(App.class.getResource("images/agenda_recipe.png"))));
        setDraggedRecipe(recipe);
        db.setContent(cb);
    }
}
