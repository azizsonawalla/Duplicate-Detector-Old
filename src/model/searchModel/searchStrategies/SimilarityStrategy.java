package model.searchModel.searchStrategies;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Finds images that are highly similar using machine learning algorithms.
 * Note: This implementation differs from other Duplicate Finders in that it looks for variations of the same image
 * (in addition to exact clones). This implementation will find modified versions of the same image (including scaled
 * up/down, cropped, colour corrected, etc.)
 */
public class SimilarityStrategy {

    public SimilarityStrategy() {
        throw new NotImplementedException();
    }
}
