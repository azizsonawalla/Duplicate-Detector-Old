package model.SearchStrategies;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.LinkedBlockingQueue;

public class HashedDuplicateFinder extends DuplicateFinder {

    /**
     * Asynchronously reads files listed in this.allFiles into this.remainingFiles, and removes paths that have
     * been read from this.allFiles. Follows a producer/consumer pattern with ``findDuplicates()``
     */
    private void readFiles() {
        // TODO
        throw new NotImplementedException();
    }
}
