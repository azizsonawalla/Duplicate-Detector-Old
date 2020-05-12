package model.async.asyncFileSystem;

import model.async.threadPool.AppThreadPool;
import model.util.Progress;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

/**
 * An asynchronous directory crawler. Recursively lists all files in directory.
 */
public class AsyncDirectoryCrawler implements Callable<List<File>> {

    private final List<File> rootDirectories;
    private final List<String> validExtensions;

    public AsyncDirectoryCrawler(List<File> rootDirectories, List<String> validExtensions) {
        this.rootDirectories = rootDirectories;
        this.validExtensions = validExtensions;
    }

    /**
     * Get stats on the number of files processed so far
     * @return Progress object with crawl progress
     */
    public Progress getProgress() {
        throw new NotImplementedException();                                                                            // TODO: Implement this
    }

    /**
     * Cancel the directory crawl
     * @throws IOException if cannot cancel
     */
    public void cancel() throws IOException {
        throw new NotImplementedException();                                                                            // TODO: Implement this
    }

    @Override
    public List<File> call() throws IOException {

        ConcurrentLinkedQueue<File> toVisit = new ConcurrentLinkedQueue<>();
        LinkedList<Future<File[]>> futures = new LinkedList<>();
        List<File> allFiles = new LinkedList<>();

        for (File rootDirectory: this.rootDirectories) {
            try {
                if (!rootDirectory.isDirectory()) {
                    throw new IOException("Root directory is not a directory");                                         // TODO: log errors
                }
                toVisit.add(rootDirectory);
            } catch (Exception e) {
                throw new IOException("Cannot read rootDirectories directory: " + e.getMessage());                        // TODO: log errors
            }
        }

        AppThreadPool threadPool = AppThreadPool.getInstance();
        while(!toVisit.isEmpty() || !futures.isEmpty()) {
            if (!toVisit.isEmpty()) {
                File thisDirectory = toVisit.poll();
                AsyncFileList loader = new AsyncFileList(thisDirectory);
                Future<File[]> future = threadPool.submit(loader);
                futures.add(future);
            } else {
                for (int i = 0; i < futures.size(); i++) {                                                              // Check if any of the futures are ready
                    Future<File[]> future = futures.get(i);
                    if (future.isDone()) {
                        try {
                            File[] unseenFiles = future.get();
                            for (File file: unseenFiles) {
                                if (file.isDirectory()) {
                                    toVisit.add(file);
                                } else if (isValidFile(file)) {
                                    allFiles.add(file);
                                    // System.out.println(file.getName());                                                 // TODO: Remove this (used for debugging)
                                }
                            }
                        } catch (Exception e) {
                            throw new IOException("AppError reading directory", e);                                        // TODO: Add better exception msg // TODO: Record errors
                        }
                        futures.remove(i);
                        break;
                    }
                }
            }
        }

        return allFiles;
    }

    private boolean isValidFile(File file) {
        if (file.isFile()) {
            if (this.validExtensions.isEmpty()) {
                return true;
            }
            String ext = getFileExtension(file).toUpperCase();
            if (this.validExtensions.contains(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the extension associated with the file without the dot. Returns empty string if file has no extension
     */
    private static String getFileExtension(File file) {
        String name = file.getName();
        String[] parts = name.split(".");
        if (parts.length > 1) {
            return parts[parts.length-1];
        }
        return "";
    }
}
