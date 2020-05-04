package model.async.FutureUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A class to manage a collection of Future objects as one Future object
 * @param <T>
 */
public abstract class FutureCollection<T> implements Future<T> {

    protected List<Future> futures;

    /**
     * Create a single Future object from a collection
     * @param futures a collection of future objects
     */
    public FutureCollection(List<Future> futures) {
        this.futures = futures;
    }

    /**
     * Calls Future.cancel() on all internal Future objects
     * @param mayInterruptIfRunning (see Future.cancel())
     * @return false if any one of the internal Future objects returns false, else true.
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        for (Future future: this.futures) {
            if (!future.cancel(mayInterruptIfRunning)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return true if all internal Future objects are cancelled, else false
     */
    @Override
    public boolean isCancelled() {
        for (Future future: this.futures) {
            if (!future.isCancelled()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return true if all internal Future objects are done, else false
     */
    @Override
    public boolean isDone() {
        for (Future future: this.futures) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calls FutureCollection.get(timeout, unit) with no timeout
     */
    @Override
    public T get() throws InterruptedException, ExecutionException {
        try {
            return get(-1, null);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);                                                                              // TODO: change this behaviour of exception conversion?
        }
    }

    @Override
    public abstract T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException;                                          // TODO: javadoc
}
