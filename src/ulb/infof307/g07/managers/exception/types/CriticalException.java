package ulb.infof307.g07.managers.exception.types;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import ulb.infof307.g07.App;
import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.ExceptionType;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Critical Exception that is unrecoverable from. Will close application. */
public class CriticalException extends ExceptionType {

    public CriticalException(String displayMessage, Throwable throwable) {
        super(displayMessage, throwable);
    }

    @Override
    public void handleException(Logger logger) {
        logger.log(Level.SEVERE, this.getDisplayMessage(), this.getThrowable());
        var message = this.getDisplayMessage() + "\n Vous pouvez inspecter le fichier de log ici:\n"
                + ExceptionManager.getInstance().getLogPath() + "\n L'application va maintenant fermer!";
        var alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setOnCloseRequest((event) -> Platform.exit());
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets()
                .add(Objects.requireNonNull(App.class.getResource("styles/main.css")).toExternalForm());
        dialogPane.getStyleClass().add("dialog");
        alert.show();
    }
}
