package ulb.infof307.g07.models.agenda.exceptions;

/**
 * Manage exception from the agenda model
 */
public class AgendaException extends Exception {

    public static final String ERROR_MSG_NAME_NULL = "Can't make an agenda with null name";
    public static final String ERROR_MSG_INVALID_DATE = "Can't make an agenda with non valid date range";
    public static final String ERROR_MSG_INVALID_MENU_DATE = "Can't add a menu with invalid date";
    public static final String ERROR_MSG_INVALID_ACTION_DATE = "Can't do an action with an invalid date";

    public AgendaException(String message) {
        super(message);
    }

    public AgendaException(String message, Throwable cause) {
        super(message, cause);
    }
}
