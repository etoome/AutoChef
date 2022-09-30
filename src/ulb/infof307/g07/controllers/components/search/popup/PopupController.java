package ulb.infof307.g07.controllers.components.search.popup;

import javafx.scene.layout.VBox;

import ulb.infof307.g07.managers.scene.SceneManager;

public class PopupController extends VBox {

    public PopupController() {
        SceneManager.getInstance().loadFxmlNode(this, "components/search_popup.fxml");
    }
}
