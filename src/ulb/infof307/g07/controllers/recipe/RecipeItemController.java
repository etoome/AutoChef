package ulb.infof307.g07.controllers.recipe;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import ulb.infof307.g07.controllers.BaseController;
import ulb.infof307.g07.controllers.agenda.RecipeStyleView;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.recipe.Recipe;
import ulb.infof307.g07.utils.Delay;

import java.util.Objects;

/**
 * Display a single recipe, with a recipe template, manages the creation of a stage for the display of the recipe.
 */
public class RecipeItemController extends AnchorPane implements BaseController {

    private final Recipe recipe;
    private int click = 0;

    @FXML
    private HBox fx_root;
    @FXML
    private ImageView fx_styleImg;
    @FXML
    private Text fx_time;
    @FXML
    private Text fx_title;
    @FXML
    private Text fx_type;

    public RecipeItemController(Recipe recipe, OnAction.OnRecipeClick clickListener) {
        this.recipe = recipe;

        SceneManager.getInstance().loadFxmlNode(this, "recipe/recipe.fxml");

        fx_styleImg
                .setImage(new Image(
                        Objects.requireNonNull(getClass().getClassLoader()
                                .getResource(RecipeStyleView.getInstance().getImgPath(recipe.getStyle()))).toString(),
                        true));

        fx_root.setOnMouseClicked(event -> {
            if (++click >= 2) {
                clickListener.execute(recipe);
            }
            Delay.wait(500, () -> click = 0);
        });

        render();
    }

    private void render() {
        fx_title.setText(recipe.getName());
        fx_type.setText(recipe.getType().toString());
        fx_time.setText(recipe.getTime().toString() + " minutes");
    }
}
