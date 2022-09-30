package ulb.infof307.g07.controllers.recipe;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import ulb.infof307.g07.controllers.BaseController;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.recipe.Ingredient;
import ulb.infof307.g07.models.recipe.IngredientUnit;

/**
 * Ingredient display controller in a recipe, could switch between view and edition mod use Ingredient model.
 */
public class IngredientController extends HBox implements BaseController {

    @FXML
    private SVGPath fx_delete;
    @FXML
    private TextField fx_name;
    @FXML
    private TextField fx_quantity;
    @FXML
    private ChoiceBox<String> fx_unit;

    private final Ingredient ingredient;
    private final OnAction.OnIngredientDelete onDelete;

    public IngredientController(Ingredient ingredient, OnAction.OnIngredientDelete onDelete) {
        this.ingredient = ingredient;
        this.onDelete = onDelete;

        SceneManager.getInstance().loadFxmlNode(this, "recipe/ingredient.fxml");

        fx_name.setOnKeyTyped(event -> {
            ingredient.setName(fx_name.getText());
            event.consume();
        });
        fx_quantity.setOnKeyTyped(event -> {
            ingredient.setQuantity(Float.parseFloat(fx_quantity.getText()));
            event.consume();
        });
        fx_unit.setOnKeyTyped(event -> {
            ingredient.setUnit(IngredientUnit.getValue(fx_unit.getValue()));
            event.consume();
        });

        render();
    }

    private void render() {
        fx_name.setText(ingredient.getName());
        fx_quantity.setText(ingredient.getQuantityValue().toString());
        fx_unit.setItems(FXCollections.observableArrayList(IngredientUnit.G.toString(), IngredientUnit.KG.toString(),
                IngredientUnit.L.toString(), IngredientUnit.ML.toString(), IngredientUnit.UNIT.toString()));
        fx_unit.getSelectionModel().select(ingredient.getUnit().toString());
    }

    @FXML
    private void deleteIngredient() {
        onDelete.execute();
    }

}
