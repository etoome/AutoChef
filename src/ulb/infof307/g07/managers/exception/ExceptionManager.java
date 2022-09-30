package ulb.infof307.g07.managers.exception;

import ulb.infof307.g07.managers.exception.types.WarningException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.*;

/**
 * Singleton for managing exceptions. Uses a logger that will write to both console and a logfile.
 */
public class ExceptionManager {
    /** The name of the logfile */
    private static final String LOG_FILENAME = "AutoChef.log";
    private static ExceptionManager instance;

    public static ExceptionManager getInstance() {
        if (instance == null) {
            instance = new ExceptionManager();
        }
        return instance;
    }

    private final Logger logger;
    private final String logPath;

    private ExceptionManager() {
        logPath = createLogPath();
        logger = createLogger(logPath);
    }

    /**
     * Create the logger. If an io exception is thrown during creation of the logfile, logging will be done only to
     * console.
     *
     * @param logPath
     *            the full path to the logfile
     *
     * @return the newly created logger
     */
    private Logger createLogger(String logPath) {
        Logger logger = Logger.getLogger(ExceptionManager.class.getName());
        logger.setLevel(Level.INFO);
        logger.addHandler(new ConsoleHandler());
        try {
            FileHandler fileHandler = new FileHandler(logPath);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException exception) {
            ExceptionManager.getInstance().handleException(new WarningException(
                    "Failed to create logfile, errors will be logged to console only!", exception));
        }
        return logger;
    }

    /** Create logfile in OS-dependent temporary directory */
    private String createLogPath() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        return Paths.get(tmpDir, LOG_FILENAME).toString();
    }

    /** Get the full path of the logfile */
    public String getLogPath() {
        return logPath;
    }

    /**
     * The bread-n-butter of the class. This is what you call when handling a try/catch.
     *
     * @param onException
     *            an instance of one of the derivatives of {@link ExceptionType}, or a lambda that takes one parameters,
     *            which is the logger.
     */
    public void handleException(OnException onException) {
        onException.handleException(logger);
    }
}
