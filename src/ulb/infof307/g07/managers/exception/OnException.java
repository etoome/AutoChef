package ulb.infof307.g07.managers.exception;

import java.util.logging.Logger;

@FunctionalInterface
public interface OnException {
    void handleException(Logger logger);
}
