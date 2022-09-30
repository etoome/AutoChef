package ulb.infof307.g07.managers.exception.types;

import ulb.infof307.g07.managers.exception.ExceptionType;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SilentException extends ExceptionType {

    public SilentException(String displayMessage, Throwable throwable) {
        super(displayMessage, throwable);
    }

    @Override
    public void handleException(Logger logger) {
        logger.log(Level.INFO, this.getDisplayMessage(), this.getThrowable());
    }
}
