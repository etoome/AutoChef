package ulb.infof307.g07.controllers.shoppinglist;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import ulb.infof307.g07.controllers.BaseController;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.recipe.IngredientUnit;
import ulb.infof307.g07.models.shoppinglist.Product;

public class CartProductController extends AnchorPane implements BaseController {

    private final Product product;
    private final Float quantity;
    private final IngredientUnit unit;

    @FXML
    private Text fx_name;

    @FXML
    private Text fx_quantity;

    @FXML
    private Text fx_unit;

    public CartProductController(Product product, Float quantity, IngredientUnit unit) {
        this.product = product;
        this.quantity = quantity;
        this.unit = unit;

        SceneManager.getInstance().loadFxmlNode(this, "shoppinglist/cart_product.fxml");

        renderComponents();
    }

    private void renderComponents() {
        int intQuantity = Math.round(quantity);
        fx_name.setText(String.valueOf(product.getName()));
        fx_quantity.setText(intQuantity == 0 ? "" : String.valueOf(intQuantity));
        fx_unit.setText(unit.toString());
    }
}
