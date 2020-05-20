package util;

public class ScanException extends RuntimeException {                                                                   // TODO: change this to regular Exception

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
