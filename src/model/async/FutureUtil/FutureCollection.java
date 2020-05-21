package model.async.FutureUtil;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A abstract class to manage a collection of Future objects as one Future object. Child classes must implement get()
 * method based on how they wish to retrieve the result from the internal Future object collection.
 * @param <T> the type of the result returned by get()
 */
public abstract class FutureCollection<T> implements Future<T> {

    protected Collection<Future> futures;

    /**
     * Create a single Future object from a collection
     * @param futures a collection of future objects
     */
    public FutureCollection(Collection<Future> futures) {
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
     * Calls FutureCollection.get(timeout, unit) with negative timeout
     */
    @Override
    public T get() throws InterruptedException, ExecutionException {
        try {
            return get(-1, null);
        } catch (TimeoutException e) {
            throw new ExecutionException(e);
        }
    }

    /**
     * Get the result from the Future object.
     * @param timeout time to wait for result. If negative, will wait indefinitely
     * @param unit unit of time for timeout (may be null if timeout is negative)
     * @return the result from the collection of Future objects
     */
    @Override
    public abstract T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException;
}
