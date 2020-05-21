package model.searchModel.searchStrategies;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Finds duplicate files by matching randomly selected pixels from the images.
 * Note: This implementation will mark images with identical content but different formats as clones (i.e. jpeg and raw
 * versions of the same image will be marked as duplicates).
 */
public class RandomPixelStrategy {

    public RandomPixelStrategy() {
        throw new NotImplementedException();
    }

}
