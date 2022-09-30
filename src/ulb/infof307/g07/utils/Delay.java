package ulb.infof307.g07.utils;

import javafx.concurrent.Task;

/** Utility class to abstract a waiting coroutine */
public class Delay {
    public static void wait(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException ignored) {
                    // only the JVM may externally SIGINT the thread
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
    }
}
