package model.async.asyncFileSystem;

import model.async.threadPool.AppThreadPool;
import model.util.Progress;

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
    private ConcurrentLinkedQueue<File> allFiles;
    private boolean interrupted = false;

    public AsyncDirectoryCrawler(List<File> rootDirectories, List<String> validExtensions) {
        this.rootDirectories = rootDirectories;
        this.validExtensions = validExtensions;
    }

    /**
     * Get stats on the number of files processed so far
     * @return Progress object with crawl progress
     */
    public Progress getProgress() {
        if (allFiles == null) {
            return new Progress(0, -1, -1, -1, -1, -1, null, null);
        }
        return new Progress(allFiles.size(), -1, -1, -1, -1, -1, null, null);                                               // TODO: add more info. Can estimate eta by calculating size differences
    }

    /**
     * Cancel the directory crawl. The future object will return null once cancelled.
     * @throws IOException if cannot cancel
     */
    public void cancel() throws IOException {
        this.interrupted = true;
    }

    @Override
    public List<File> call() throws IOException {                                                                       // TODO: break this down into helpers
        ConcurrentLinkedQueue<File> toVisit = new ConcurrentLinkedQueue<>(getDirectoriesOnly(this.rootDirectories));
        LinkedList<Future<File[]>> visitors = new LinkedList<>();
        allFiles = new ConcurrentLinkedQueue<>();

        if (toVisit.size() != this.rootDirectories.size()) {
            throw new IOException("Some files given are not directories");                                              // TODO: log errors
        }

        while(!toVisit.isEmpty() || !visitors.isEmpty()) {
            if (interrupted) {
                return null;
            }
            if (!toVisit.isEmpty()) {
                Future<File[]> visitor = createVisitor(toVisit.poll());
                visitors.add(visitor);
            } else {
                for (int i = 0; i < visitors.size(); i++) {
                    if (interrupted) {
                        return null;
                    }
                    Future<File[]> visitor = visitors.get(i);
                    if (visitor.isDone()) {
                        parseVisitorResults(visitor, toVisit);
                        visitors.remove(i);
                        break;
                    }
                }
            }
        }
        return new LinkedList<>(allFiles);
    }

    private void parseVisitorResults(Future<File[]> visitor, ConcurrentLinkedQueue<File> toVisit) throws IOException {
        File[] results;
        try {
            results = visitor.get();
        } catch (Exception e) {
            e.printStackTrace();                                                                                        // TODO: error handling
            throw new IOException("Error reading directory", e);
        }

        for (File file: results) {
            if (file.isDirectory()) {
                toVisit.add(file);
            } else if (isValidFile(file)) {
                allFiles.add(file);
            }
        }
    }

    private Future<File[]> createVisitor(File dir) {
        AsyncFileList loader = new AsyncFileList(dir);
        return AppThreadPool.getInstance().submit(loader);
    }

    /**
     * Filters given list and keeps only directories
     * @param files list to filter
     * @return directories from given list
     */
    private List<File> getDirectoriesOnly(List<File> files) {
        List<File> filtered = new LinkedList<>();
        files.forEach((File f) -> {
            if (f.isDirectory()) {
                filtered.add(f);
            }
        });
        return filtered;
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
