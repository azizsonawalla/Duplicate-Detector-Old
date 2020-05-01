package model.SearchStrategies;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DuplicateFinder {

    private final String rootDirectory;                          // Root directory with duplicates
    private List<String> allFilePaths;                           // Absolute paths for all files in Root directory
    private long startTime;                                      // epoch time for when last search was started
    private BlockingQueue<File> remainingFiles;                  // Thread-safe queue to read files from disk
    private ConcurrentHashMap<String, List<String>> duplicates;  // Thread-safe map to store duplicate files

    public DuplicateFinder(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    /**
     * Start the search for duplicates in the root folder. This method searches for duplicates asynchronously and will
     * return immediately after triggering the search. To get progress stats for the search, call ``getProgress()`` and
     * to interrupt the search call ``stopSearch()``
     * @throws SearchException if there's an error starting the search
     */
    public abstract void startSearch() throws SearchException;

    /**
     * Stops an ongoing search and halts all asynchronous tasks. Does nothing if no search is in progress.
     * @throws SearchException if there's an error stopping the search
     */
    public abstract void stopSearch() throws SearchException;

    /**
     * Get the status of the current search.
     * @return Progress of the current search.
     * @throws SearchException if no search is in progress
     */
    public abstract Progress getProgress() throws SearchException;

    /**
     * Get results from the search.
     * @return a 2-D list where each inner list is a collection of paths to files that are suspected duplicates
     * @throws SearchException if search is still ongoing
     */
    public List<List<String>> getResults() throws SearchException {
        // TODO
        throw new NotImplementedException();
    }

    /**
     * Expands all file paths under rootDirectory to absolute paths and returns them in a list. Also filters out files
     * that don't match extensions under validExtensions
     * @param rootDirectory directory to expand
     * @param validExtensions files with these extensions will be returned. Pass empty list to keep all files.
     * @return list of all files (including files under subfolders) that match validExtensions
     */
    private static List<String> expandPaths(String rootDirectory, List<String> validExtensions) {
        // TODO
        throw new NotImplementedException();
    }

    /**
     * Asynchronously reads files listed in this.allFilePaths into this.remainingFiles, and removes paths that have
     * been read from this.allFilePaths. Follows a producer/consumer pattern with ``findDuplicates()``
     */
    private void readFiles() {
        // TODO
        throw new NotImplementedException();
    }

    /**
     * Asynchronously reads files from this.remainingFiles and stores duplicate files in this.duplicates
     */
    private void findDuplicates() {
        // TODO
        throw new NotImplementedException();
    }
}
