package ulb.infof307.g07.utils;

import org.apache.commons.lang.NullArgumentException;

import java.util.Objects;

/**
 * Singleton that contains information about the configuration of the project
 */
public class Config {
    private static Config instance = null;

    /** Factory to allow for delayed config initialisation */
    private static final ConfigFactory configFactory = new ConfigFactory();

    /** The running environment of the project */
    private final Environment environment;

    private Boolean isAppRunningFromJar = null;

    /** Lazy init the singleton with the help of the factory */
    public static Config getInstance() {
        if (instance == null) {
            instance = configFactory.create();
        }
        return instance;
    }

    /** Set the environment prior to creation of the singleton */
    public static void setEnvironment(Environment newEnv) {
        if (instance != null) {
            throw new ChangeConfigRuntimeException("Cannot change environment after first access to the singleton!");
        }
        if (newEnv == null) {
            throw new NullArgumentException("Environment cannot be null!");
        }
        configFactory.setEnv(newEnv);
    }

    private Config(Environment newEnvironment) {
        environment = newEnvironment;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public static boolean isPreconfiguredEnvironmentEqualTo(Environment environment) {
        return configFactory.env.equals(environment);
    }

    /** Determine if we are running the application from the jar by parsing the resource path */
    public boolean isAppRunningFromJar() {
        if (isAppRunningFromJar == null) {
            var url = Config.class.getResource("/" + Config.class.getName().replace('.', '/') + ".class");
            if (url == null) {
                throw new RuntimeException("Couldn't check if running inside a jar or not, url was null!");
            }
            String protocol = Objects.requireNonNull(url.getProtocol());
            isAppRunningFromJar = protocol.equals("jar");
        }
        return isAppRunningFromJar;
    }

    public enum Environment {
        RUNTIME, TESTING
    }

    private static class ConfigFactory {

        private Environment env = Environment.RUNTIME;

        public Config create() {
            return new Config(env);
        }

        public void setEnv(Environment newEnv) {
            env = newEnv;
        }
    }
}
