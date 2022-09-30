package ulb.infof307.g07.controllers.map;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.recipe.Quantity;
import ulb.infof307.g07.models.shoppinglist.Product;

public class ShoppingListItem extends HBox {
    @FXML
    private Label fx_shoppingListProduct;
    @FXML
    private Label fx_shoppingListQuantity;

    public ShoppingListItem(Product product, Quantity quantity) {
        SceneManager.getInstance().loadFxmlNode(this, "map/product_shopping_list.fxml");
        fx_shoppingListProduct.setText(product.getName());
        fx_shoppingListQuantity.setText((int) quantity.getQuantity() + quantity.getUnit().toString());
    }

}
