package ulb.infof307.g07.models.agenda.exceptions;

/**
 * Manage exception from the menu model
 */
public class MenuException extends Exception {

    public MenuException(String message) {
        super(message);
    }

    public MenuException(String message, Throwable cause) {
        super(message, cause);
    }
}
