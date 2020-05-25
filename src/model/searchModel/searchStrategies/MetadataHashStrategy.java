package model.searchModel.searchStrategies;

import java.io.File;

/**
 * Finds duplicate files based on file metadata.
 *
 * Definition of duplicate file: files with the same name and size
 */
public class MetadataHashStrategy extends HashingStrategy {

    /**
     * Creates a hashcode for the file using its name and size
     * @param file file to hash
     * @return hash as a string
     */
    @Override
    String hash(File file) {
        long nameHash = file.getName().hashCode();
        long size = file.length();
        return String.format("%s_%s", nameHash, Long.toString(size));
    }
}
