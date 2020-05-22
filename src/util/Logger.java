package util;

import config.Config;

/**
 * A simple logger. Messages are prepended with calling class' name and the log level.
 */
public class Logger {

    /**
     * Logging levels in order of most to least detail. A log level includes all log levels below it. For example,
     * setting the log level to DEBUG will print INFO and ERROR statements too, and setting the level to INFO will also
     * print ERROR statements, but not DEBUG statements.
     */
    public enum Level {
        DEBUG,
        INFO,
        ERROR,
        NONE
    }

    private final Level level;
    private final Class caller;

    /**
     * Create a Logger unique to the calling class. Messages will be prepended with the calling class' name.
     * @param caller the calling class
     */
    public Logger(Class caller) {
        this.caller = caller;
        this.level = Config.LOG_LEVEL;
    }

    /**
     * Print a debug statement
     * @param msg message to print
     */
    public void debug(String msg) {
        if (level == Level.DEBUG) {
            print("DEBUG: " + msg);
        }
    }

    /**
     * Print an info statement
     * @param msg message to print
     */
    public void info(String msg) {
        if (level == Level.DEBUG || level == Level.INFO) {
            print("INFO: " + msg);
        }
    }

    /**
     * Print an error statement
     * @param msg message to print
     */
    public void error(String msg) {
        if (level == Level.ERROR || level == Level.DEBUG || level == Level.INFO) {
            print("ERROR: " + msg);
        }
    }

    /**
     * Prepend the message with the caller's name and print it to stdout
     * @param msg message to print
     */
    private void print(String msg) {
        System.out.println(String.format("(%s) %s", caller.getName(), msg));
    }
}
