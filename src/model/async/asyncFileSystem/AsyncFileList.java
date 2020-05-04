package model.async.asyncFileSystem;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.concurrent.Callable;

/**
 * An asynchronous file list loader
 */
class AsyncFileList implements Callable<File[]> {
    private File file;

    AsyncFileList(File file) {
        this.file = file;
    }

    @Override
    public File[] call() throws Exception {
        if (!file.isDirectory()) {
            throw new InvalidParameterException("Given file is not a directory");
        }
        File[] subFiles = file.listFiles();
        if (subFiles != null) {
            return subFiles;
        }
        return new File[0];
    }
}
