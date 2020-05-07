package model.util;

public class ScanException extends RuntimeException {

    public ScanException() {
        super();
    }

    public ScanException(String msg) {
        super(msg);
    }

    public ScanException(String msg, Throwable e) {
        super(msg, e);
    }
}