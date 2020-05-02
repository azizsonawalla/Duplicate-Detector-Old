package model.util;

public class SearchException extends RuntimeException {

    public SearchException() {
        super();
    }

    public SearchException(String msg) {
        super(msg);
    }

    public SearchException(String msg, Throwable e) {
        super(msg, e);
    }
}
