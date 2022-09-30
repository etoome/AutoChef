package ulb.infof307.g07.controllers.shoppinglist;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import ulb.infof307.g07.controllers.BaseController;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;

public class ArchivedController extends AnchorPane implements BaseController {

    private final onArchivedClick clicklistener;

    @FXML
    private Text fx_title;

    public ArchivedController(ShoppingList shoppinglist, onArchivedClick clicklistener) {
        this.clicklistener = clicklistener;

        SceneManager.getInstance().loadFxmlNode(this, "shoppinglist/archived.fxml");

        fx_title.setText(shoppinglist.getName());
    }

    @FXML
    private void OnUnarchive() {
        clicklistener.execute();
    }
}
