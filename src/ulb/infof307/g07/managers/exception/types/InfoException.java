package ulb.infof307.g07.managers.exception.types;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import ulb.infof307.g07.App;
import ulb.infof307.g07.managers.exception.ExceptionType;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Information exception, everything is ok, but we just want to inform the user of something.
 */
public class InfoException extends ExceptionType {

    public InfoException(String displayMessage, Throwable throwable) {
        super(displayMessage, throwable);
    }

    @Override
    public void handleException(Logger logger) {
        logger.log(Level.INFO, this.getDisplayMessage(), this.getThrowable());
        Alert alert = new Alert(Alert.AlertType.INFORMATION, this.getDisplayMessage(), ButtonType.CLOSE);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets()
                .add(Objects.requireNonNull(App.class.getResource("styles/main.css")).toExternalForm());
        dialogPane.getStyleClass().add("dialog");
        alert.show();
    }
}
