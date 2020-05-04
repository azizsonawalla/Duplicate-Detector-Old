package model.async.FutureUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class FutureCollection<T> implements Future<T> {                                                        // TODO: javadoc

    protected List<Future> futures;

    public FutureCollection(List<Future> futures) {                                                                     // TODO: javadoc
        this.futures = futures;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {                                                              // TODO: javadoc
        for (Future future: this.futures) {
            if (!future.cancel(mayInterruptIfRunning)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isCancelled() {                                                                                      // TODO: javadoc
        for (Future future: this.futures) {
            if (!future.isCancelled()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isDone() {                                                                                           // TODO: javadoc
        for (Future future: this.futures) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException                   {                                  // TODO: javadoc
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
