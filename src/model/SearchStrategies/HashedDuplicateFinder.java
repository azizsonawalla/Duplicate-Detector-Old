package model.SearchStrategies;

import model.util.Progress;
import model.util.SearchException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class HashedDuplicateFinder extends DuplicateFinder {

    public HashedDuplicateFinder() {
        super("");
    }

    /**
     * Asynchronously reads files listed in this.allFiles into this.remainingFiles, and removes paths that have
     * been read from this.allFiles. Follows a producer/consumer pattern with ``findDuplicates()``
     */
    private void readFiles() {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public void startSearch() throws SearchException {

    }

    @Override
    public void stopSearch() throws SearchException {

    }

    @Override
    public Progress getProgress() throws SearchException {
        return null;
    }

    @Override
    protected void findDuplicates() {

    }
}
