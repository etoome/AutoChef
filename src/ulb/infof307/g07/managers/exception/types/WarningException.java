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
 * Warning exception, notify the user that something went wrong, but no actions needs to be taken.
 */
public class WarningException extends ExceptionType {
    public WarningException(String displayMessage, Throwable throwable) {
        super(displayMessage, throwable);
    }

    @Override
    public void handleException(Logger logger) {
        logger.log(Level.WARNING, this.getDisplayMessage(), this.getThrowable());
        Alert alert = new Alert(Alert.AlertType.WARNING, this.getDisplayMessage(), ButtonType.CLOSE);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets()
                .add(Objects.requireNonNull(App.class.getResource("styles/main.css")).toExternalForm());
        dialogPane.getStyleClass().add("dialog");
        alert.show();
    }
}
