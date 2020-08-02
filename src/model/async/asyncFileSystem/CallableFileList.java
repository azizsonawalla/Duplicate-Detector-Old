package model.async.asyncFileSystem;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.concurrent.Callable;

/**
 * A callable file list loader. Implements the Callable interface to be able to run this in a background thread.
 */
class CallableFileList implements Callable<File[]> {
    private File file;

    /**
     * Creates a file list loader
     * @param file file to read from
     */
    CallableFileList(File file) {
        this.file = file;
    }


    /**
     * Initiate read of file list
     * @return files within the given file
     * @throws InvalidParameterException if the given file is invalid
     * @throws IOException if there is a problem reading the list of files
     */
    @Override
    public File[] call() throws InvalidParameterException, IOException {
        if (!file.isDirectory()) {
            throw new InvalidParameterException("Given file is not a directory");
        }
        try {
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                return subFiles;
            }
            return new File[0];
        } catch (Exception e) {
            throw new IOException("Failed to read " + file.getName(), e);
        }
    }
}
