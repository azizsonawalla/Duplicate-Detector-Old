package model.SearchStrategies;

import config.Config;
import model.async.asyncFileSystem.AsyncDirectoryCrawler;
import model.async.workerPool.WorkerPool;
import model.util.Progress;
import model.util.SearchException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * An abstract strategy class for finding duplicate files. Contains common methods for running file comparison search.
 */
public abstract class DuplicateFinder {

    private AsyncDirectoryCrawler crawler;                                                                              // Async crawler for allFiles
    private Future<List<File>> allFilesFuture;                                                                          // Future object for allFiles
    private ConcurrentLinkedQueue<File> allFiles;                                                                       // All files in Root directory

    private final String rootDirectory;                                                                                 // Root directory with duplicates
    private long startTime;                                                                                             // epoch time for when last search was started
    private ConcurrentHashMap<String, List<File>> duplicates;                                                           // Thread-safe map to store duplicate files
    private WorkerPool threadPool = WorkerPool.getInstance();                                                           // Background worker thread pool for application

    public DuplicateFinder(String rootDirectory) {                                                                      // TODO: Add the ability to pass multiple roots
        this.allFiles = new ConcurrentLinkedQueue<>();
        this.rootDirectory = rootDirectory;
        crawler = new AsyncDirectoryCrawler(this.rootDirectory, Config.SUPPORTED_FILE_TYPES);
        this.allFilesFuture = threadPool.submit(crawler);
    }

    /**
     * Get the progress on pre-search tasks
     * @return Progress object with current pre-search stats
     * @throws SearchException if search has already started or completed
     */
    public Progress getPreSearchProgress() throws SearchException {
        if (searchInProgress() || searchDone()) {
            throw new SearchException("Search has already started.");
        }
        return crawler.getProgress();
    }

    /**
     * Start the search for duplicates in the root folder. This method searches for duplicates asynchronously and will
     * return immediately after triggering the search. To get progress stats for the search, call ``getProgress()`` and
     * to interrupt the search call ``stopSearch()``
     * @throws SearchException if there's an error starting the search
     */
    public void startSearch() throws SearchException {
        if (searchInProgress()) {
            throw new SearchException("A search is already in progress");
        }
        try {
            this.allFiles = new ConcurrentLinkedQueue<>(allFilesFuture.get());  // blocks until Future is ready
        } catch (Exception e) {
            throw new SearchException("Failed to read all files from Future", e);
        }
        this.startTime = System.currentTimeMillis();
        this.findDuplicates();
    }

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
    public abstract Progress getSearchProgress() throws SearchException;

    /**
     * Get results from the search.
     * @return a 2-D list where each inner list is a collection of File objects that are suspected duplicates
     * @throws SearchException if search is still ongoing
     */
    public List<List<File>> getResults() throws SearchException {
        if (!searchDone()) {
            throw new SearchException("Search is still in progress. Cannot return results.");
        }
        ArrayList<List<File>> results = new ArrayList<>();
        for (List<File> duplicateSet: duplicates.values()) {
            if (duplicateSet.size() > 1) {
                results.add(duplicateSet);
            }
        }
        return results;
    }

    /**
     * Asynchronously reads files from this.remainingFiles and stores duplicate files in this.duplicates
     */
    protected abstract void findDuplicates();

    /**
     * @return true if search is complete, else false
     */
    private boolean searchDone() {                                                                                      // TODO: Implement this
        throw new NotImplementedException();
    }

    /**
     * @return true if search is in progress
     */
    private boolean searchInProgress() {                                                                                // TODO: Implement this
        throw new NotImplementedException();
    }
}
