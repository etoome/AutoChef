package ulb.infof307.g07.controllers.components;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.util.Duration;
import ulb.infof307.g07.managers.scene.SceneManager;

/**
 * A temporary object used to display information to the user, Used when it's not model information but a bad action
 * from the user, when it's a model error use the ExceptionManager.
 */
public class PopupInformation extends Popup {

    public PopupInformation(Styles popupStyle, String popupText) {
        String[] colors = { "#D50000", "#FF6D00", "#00C853" };
        this.setAutoHide(true);
        this.setHideOnEscape(true);

        Label infoText = new Label(popupText);
        infoText.setFont(new Font("Tahoma", 24));
        infoText.setTextFill(Color.web(colors[popupStyle.ordinal()]));

        FadeTransition ft = new FadeTransition(Duration.millis(3000), infoText);
        ft.setFromValue(1);
        ft.setToValue(0.);
        ft.play();

        this.getContent().add(infoText);

        this.setX(SceneManager.getInstance().getMainStage().getX()
                + SceneManager.getInstance().getMainStage().getWidth() / 3);
        this.setY(SceneManager.getInstance().getMainStage().getY()
                + SceneManager.getInstance().getMainStage().getHeight() - 90);
        this.show(SceneManager.getInstance().getMainStage());
    }

    public enum Styles {
        ERROR_POPUP, UPDATE_POPUP, SAVE_POPUP
    }

}
