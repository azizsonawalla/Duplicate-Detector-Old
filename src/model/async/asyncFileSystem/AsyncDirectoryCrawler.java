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

    private final String rootDirectory;
    private final List<String> validExtensions;

    public AsyncDirectoryCrawler(String rootDirectory, List<String> validExtensions) {                                  // TODO: Accept multiple roots
        this.rootDirectory = rootDirectory;
        this.validExtensions = validExtensions;
    }

    /**
     * Get stats on the number of files processed so far
     * @return Progress object with crawl progress
     */
    public Progress getProgress() {
        throw new NotImplementedException();                                                                            // TODO: Implement this
    }

    @Override
    public List<File> call() throws Exception {

        ConcurrentLinkedQueue<File> toVisit = new ConcurrentLinkedQueue<>();
        LinkedList<Future<File[]>> futures = new LinkedList<>();
        List<File> allFiles = new LinkedList<>();

        try {
            File root = new File(this.rootDirectory);
            if (!root.isDirectory()) {
                throw new IOException("Root directory is not a directory");
            }
            toVisit.add(root);
        } catch (Exception e) {
            throw new IOException("Cannot read rootDirectory directory: " + e.getMessage());
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
                            throw new IOException("Error reading directory", e);                                        // TODO: Add better exception msg // TODO: Record errors
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
