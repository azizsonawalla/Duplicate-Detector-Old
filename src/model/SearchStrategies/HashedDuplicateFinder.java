package model.SearchStrategies;

import model.util.Progress;
import model.util.SearchException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class HashedDuplicateFinder { //extends DuplicateFinder {

    public HashedDuplicateFinder() {
    }

    /**
     * Asynchronously reads files listed in this.allFiles into this.remainingFiles, and removes paths that have
     * been read from this.allFiles. Follows a producer/consumer pattern with ``findDuplicates()``
     */
    private void readFiles() {
        // TODO
        throw new NotImplementedException();
    }
}
