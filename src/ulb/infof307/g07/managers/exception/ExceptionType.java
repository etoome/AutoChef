package ulb.infof307.g07.managers.exception;

/**
 * A base abstract class that implements OnException interface. Derive of this class to create different type of
 * exceptions.
 */
public abstract class ExceptionType implements OnException {
    private final String displayMessage;
    private final Throwable throwable;

    protected ExceptionType(String displayMessage, Throwable throwable) {
        this.displayMessage = displayMessage;
        this.throwable = throwable;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
