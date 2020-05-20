package util;

import config.Config;

/**
 * A simple logger
 */
public class Logger {

    public enum Level {
        DEBUG, INFO, ERROR, NONE
    }

    private final Level level;
    private final Class caller;

    public Logger(Class caller) {
        this.caller = caller;
        this.level = Config.LOG_LEVEL;
    }
    
    public void debug(String msg) {
        if (level == Level.DEBUG || level == Level.INFO || level == Level.ERROR) {
            print("DEBUG: " + msg);
        }
    }
    
    public void info(String msg) {
        if (level == Level.INFO || level == Level.ERROR) {
            print("INFO: " + msg);
        }
    }
    
    public void error(String msg) {
        if (level == Level.ERROR) {
            print("ERROR: " + msg);
        }
    }
    
    private void print(String msg) {
        System.out.println(String.format("(%s) %s", caller.getName(), msg));
    }
}
