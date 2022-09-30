package ulb.infof307.g07.models.store.exceptions;

public class StoreException extends Exception {

    public StoreException(String message) {
        super(message);
    }

    public StoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
