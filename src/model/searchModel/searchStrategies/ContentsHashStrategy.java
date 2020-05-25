package model.searchModel.searchStrategies;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Finds duplicate files based on file contents hash
 */
public class ContentsHashStrategy extends HashingStrategy {

    /**
     * Creates a hashcode for the file using its full contents
     * @param file file to hash
     * @return hash as a string
     */
    @Override
    String hash(File file) throws IOException {
        InputStream is = Files.newInputStream(file.toPath());
        return org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
    }
}
