package ulb.infof307.g07.utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/** Utility class to check if we are connected to the internet, used for the map */
public class InternetConnectionTester {
    public static Boolean isConnected() {
        // found on https://www.tutorialspoint.com/Checking-internet-connectivity-in-Java
        try {
            URL url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            return true;
        } catch (IOException b) {
            return false;
        }
    }
}
