package model.async.asyncFileSystem;

import model.async.threadPool.AppThreadPool;
import util.Progress;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import static util.FileSystemUtil.getFileExtension;

/**
 * An asynchronous directory crawler. Recursively lists all files in the given directories, and filters for valid file
 * extensions. Implements the Callable interface to be able to run the crawl in a background thread.
 */
public class AsyncDirectoryCrawler implements Callable<List<File>> {

    /**
     * The current stage of the directory crawl
     */
    private enum CrawlStage {
        NOT_STARTED, IN_PROGRESS, DONE, CANCELLED
    }

    private final List<File> rootDirectories;
    private final List<String> validExtensions;
    private ConcurrentLinkedQueue<File> allFiles;
    private LinkedList<Exception> errors = new LinkedList<>();

    private boolean interrupted = false;
    private CrawlStage currentStage = CrawlStage.NOT_STARTED;

    /**
     * Creates an asynchronous directory crawler.
     * @param rootDirectories the directories to crawl through
     * @param validExtensions only files matching these extensions will be returned. Empty list will return all files
     *                        (i.e. no filtering). eg. input = ("JPEG", "PNG", "PDF")
     */
    public AsyncDirectoryCrawler(List<File> rootDirectories, List<String> validExtensions) {
        this.rootDirectories = rootDirectories;
        this.validExtensions = validExtensions;
        this.validExtensions.forEach(AsyncDirectoryCrawler::cleanExtension);
        this.allFiles = new ConcurrentLinkedQueue<>();
    }

    /**
     * Get stats on the progress of the directory crawl
     * @return Progress object with stats
     */
    public Progress getProgress() {
        if (currentStage == CrawlStage.CANCELLED) {
            return new Progress(-1, -1, -1, -1, -1, null, currentStage.toString());
        }
        if (currentStage == CrawlStage.NOT_STARTED) {
            return new Progress(0, 0, -1, -1, -1, errors, currentStage.toString());
        }
        if (currentStage == CrawlStage.DONE) {
            return new Progress(allFiles.size(),0,0,-1,0, errors, currentStage.toString());
        }
        return new Progress(allFiles.size(),-1,-1,-1,-1, errors, currentStage.toString());
    }

    /**
     * Cancel the directory crawl. The call() method will return null once cancelled.
     */
    public void cancel() {
        this.interrupted = true;
        currentStage = CrawlStage.CANCELLED;
    }

    /**
     * Begin the directory crawl.
     * @return the list of all files under the given directories
     */
    @Override
    public List<File> call() {
        currentStage = CrawlStage.IN_PROGRESS;

        ConcurrentLinkedQueue<File> toVisit = new ConcurrentLinkedQueue<>(getDirectoriesOnly(this.rootDirectories));
        LinkedList<Future<File[]>> visitors = new LinkedList<>();

        if (toVisit.size() != this.rootDirectories.size()) {
            errors.add(new InvalidParameterException("Some files given are not directories"));
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
                        try {
                            parseVisitorResults(visitor, toVisit, allFiles);
                        } catch (Exception e) {
                            errors.add(e);
                        }
                        visitors.remove(i);
                        break;
                    }
                }
            }
        }

        currentStage = CrawlStage.DONE;
        return new LinkedList<>(allFiles);
    }

    /**
     * Gets and filters the results from a directory visitor. Adds valid files to allFiles and directories to toVisit
     * @param visitor the visitor with directory crawl results
     * @param toVisit directories from results will be added to this
     * @param allFiles valid files from results will be added to this
     */
    private void parseVisitorResults(Future<File[]> visitor, Collection<File> toVisit,
                                     Collection<File> allFiles) throws Exception {
        File[] results;
        results = visitor.get();

        for (File file: results) {
            if (file.isDirectory()) {
                toVisit.add(file);
            } else if (isValidFile(file)) {
                allFiles.add(file);
            }
        }
    }

    /**
     * Creates an asynchronously populated Future object the list of files within a directory
     * @param dir directory to visit
     * @return a Future object for the list of files within the directory
     */
    private Future<File[]> createVisitor(File dir) {
        CallableFileList loader = new CallableFileList(dir);
        return AppThreadPool.getInstance().submit(loader);
    }

    /**
     * Filters given list of files and returns the ones that are directories
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

    /**
     * Checks if the given File object is a file (not directory) and has a valid extension
     * @param file File object to inspect
     * @return true if file is valid, false otherwise
     */
    private boolean isValidFile(File file) {
        if (file.isFile()) {
            if (this.validExtensions.isEmpty()) {
                return true;
            }
            String ext = cleanExtension(getFileExtension(file));
            for (String vExt: this.validExtensions) {
                if (vExt.equals(ext)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Cleans file extensions in a standardized way
     * @param dExt extension to clean
     * @return the given extension without the dot ('.') and in upper case
     */
    private static String cleanExtension(String dExt) {
        return dExt.toUpperCase().replace("\\.", "").trim();
    }
}
