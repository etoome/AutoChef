package ulb.infof307.g07.controllers.agenda.popup;

import javafx.scene.Node;
import javafx.stage.Popup;
import ulb.infof307.g07.managers.scene.SceneManager;

/**
 * Handle all popups from the agenda, inherited from javafx.stage.Popup, simplifies calls
 */
public class PopupController extends Popup {

    public PopupController(Node content) {
        this.setAutoHide(true);
        this.setHideOnEscape(true);
        this.getContent().add(content);
    }

    public void showPopup(double posX, double posY) {
        this.setX(posX);
        this.setY(posY);
        this.show(SceneManager.getInstance().getMainStage());
    }

    public void setContent(Node content) {
        this.getContent().add(content);
    }

}
