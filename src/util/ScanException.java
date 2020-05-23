package util;

public class ScanException extends Exception {

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
