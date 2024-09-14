package alejandro.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Logs {
    public static void logINFO(Object object, String msg) {
        Logger.getLogger(object.getClass().getName()).log(Level.INFO, String.valueOf(msg));
    }

    public static void logWARNING(Object object, String msg, Exception e) {
        Logger.getLogger(object.getClass().getName()).log(Level.WARNING, msg, e);
    }
}
