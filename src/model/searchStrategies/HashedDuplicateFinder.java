package model.searchStrategies;

import model.util.Progress;
import model.util.SearchException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Finds duplicate files based on file contents hash
 */
public class HashedDuplicateFinder { // extends DuplicateFinder {

    public HashedDuplicateFinder(String rootDirectory) {
        //super(rootDirectory);
    }

    //@Override
    public void stopSearch() throws SearchException {                                                                   // TODO: implement this
        throw new NotImplementedException();
    }

    //@Override
    public Progress getSearchProgress() throws SearchException {                                                        // TODO: implement this
        throw new NotImplementedException();
    }

    //@Override
    protected void findDuplicates() {                                                                                   // TODO: implement this
        throw new NotImplementedException();
    }
}
