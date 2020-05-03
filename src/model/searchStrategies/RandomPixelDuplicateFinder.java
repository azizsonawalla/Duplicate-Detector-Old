package model.searchStrategies;

import model.util.Progress;
import model.util.SearchException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Finds duplicate files by matching randomly selected pixels from the images.
 * Note: This implementation will mark images with identical content but different formats as clones (i.e. jpeg and raw
 * versions of the same image will be marked as duplicates).
 */
public class RandomPixelDuplicateFinder  { // extends DuplicateFinder {

    public RandomPixelDuplicateFinder(String rootDirectory) {
        // super(rootDirectory);
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
