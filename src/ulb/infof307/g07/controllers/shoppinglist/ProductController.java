package ulb.infof307.g07.controllers.shoppinglist;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import ulb.infof307.g07.controllers.BaseController;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.shoppinglist.Product;
import ulb.infof307.g07.utils.Colors;

public class ProductController extends AnchorPane implements BaseController {

    private final Product product;
    private Float quantity;
    private final onProductClick clicklistener;

    @FXML
    private SVGPath fx_less;

    @FXML
    private SVGPath fx_more;

    @FXML
    private Text fx_name;

    @FXML
    private TextField fx_quantity;

    public ProductController(Product product, Float quantity, onProductClick clicklistener) {
        this.product = product;
        this.quantity = quantity;
        this.clicklistener = clicklistener;

        SceneManager.getInstance().loadFxmlNode(this, "shoppinglist/product.fxml");

        fx_name.setText(String.valueOf(product.getName()));
        renderQuantity();
    }

    @FXML
    private void OnLess() {
        updateInputQuantity(getInputQuantity() - 1);
    }

    @FXML
    private void change() {
        updateInputQuantity(getInputQuantity());
    }

    @FXML
    private void OnMore() {
        updateInputQuantity(getInputQuantity() + 1);
    }

    private Float getInputQuantity() {
        String text = fx_quantity.getText();
        float quantity = 0F;
        if (text.matches("[0-9]+(\\.[0-9]*)?")) {
            quantity = Float.parseFloat(text);
        }
        return quantity;
    }

    private void updateInputQuantity(Float quantity) {
        clicklistener.execute(product, quantity);
        this.quantity = quantity;
        renderQuantity();
    }

    private void renderQuantity() {
        int intQuantity = Math.round(quantity);
        if (intQuantity <= 0) {
            fx_quantity.setText("");
            fx_less.setDisable(true);
            fx_less.setFill(Colors.DISABLE.getColor());
        } else {
            fx_quantity.setText(String.valueOf(intQuantity));
            fx_less.setDisable(false);
            fx_less.setFill(Colors.ENABLE.getColor());
        }
    }
}
