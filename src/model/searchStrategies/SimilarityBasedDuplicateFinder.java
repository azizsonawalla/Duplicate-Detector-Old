package model.searchStrategies;

import model.util.Progress;
import model.util.SearchException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Finds images that are highly similar using machine learning algorithms.
 * Note: This implementation differs from other Duplicate Finders in that it looks for variations of the same image
 * (in addition to exact clones). This implementation will find modified versions of the same image (including scaled
 * up/down, cropped, colour corrected, etc.)
 */
public class SimilarityBasedDuplicateFinder { // extends DuplicateFinder {

    public SimilarityBasedDuplicateFinder(String rootDirectory) {
        // super(rootDirectory);
    }

    // @Override
    public void stopSearch() throws SearchException {                                                                   // TODO: implement this
        throw new NotImplementedException();
    }

    // @Override
    public Progress getSearchProgress() throws SearchException {                                                        // TODO: implement this
        throw new NotImplementedException();
    }

    // @Override
    protected void findDuplicates() {                                                                                   // TODO: implement this
        throw new NotImplementedException();
    }
}
