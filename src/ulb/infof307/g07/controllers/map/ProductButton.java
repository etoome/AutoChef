package ulb.infof307.g07.controllers.map;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import ulb.infof307.g07.managers.scene.SceneManager;

public class ProductButton extends HBox {
    @FXML
    private Label fx_productName;
    @FXML
    private Label fx_productPrice;

    public ProductButton(String name, Float price) {
        SceneManager.getInstance().loadFxmlNode(this, "map/product.fxml");
        fx_productName.setText(name);
        fx_productPrice.setText(price.toString());
    }

}
