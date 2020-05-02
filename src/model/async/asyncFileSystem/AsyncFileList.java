package model.async.asyncFileSystem;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * An asynchronous file loader
 */
class AsyncFileList implements Callable<File[]> {
    private File file;

    AsyncFileList(File file) {
        this.file = file;
    }

    @Override
    public File[] call() throws Exception {
        File[] subFiles = file.listFiles();
        if (subFiles != null) {
            return subFiles;
        }
        return new File[0];
    }
}
