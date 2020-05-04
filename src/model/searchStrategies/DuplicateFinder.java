package model.searchStrategies;

import config.Config;
import model.async.asyncFileSystem.AsyncDirectoryCrawler;
import model.async.threadPool.AppThreadPool;
import model.util.Progress;
import model.util.SearchException;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

/**
 * An abstract strategy class for finding duplicate files. Contains common methods for running file comparison search.
 */
public abstract class DuplicateFinder {

    /* Flags and timestamps */
    private boolean searchInProgress = false;
    private boolean searchDone = false;
    private long startTime;                                                                                             // epoch time for when last search was started

    /* Pre-search objects */
    private AsyncDirectoryCrawler crawler;                                                                              // Async crawler for allFiles
    private Future<List<File>> allFilesFuture;                                                                          // Future object for allFiles

    /* Search objects */
    private Future<List<List<File>>> duplicatesFuture;                                                                  // Future object for sets of duplicate files


    public DuplicateFinder(String rootDirectory) {                                                                      // TODO: Add the ability to pass multiple roots
        crawler = new AsyncDirectoryCrawler(rootDirectory, Config.SUPPORTED_FILE_TYPES);
        this.allFilesFuture = AppThreadPool.getInstance().submit(crawler);
    }

    /**
     * Get the progress on pre-search tasks
     * @return Progress object with current pre-search stats
     * @throws SearchException if search has already started or completed
     */
    public Progress getPreSearchProgress() throws SearchException {
        if (isSearchInProgress() || isSearchDone()) {
            throw new SearchException("Search has already started.");
        }
        return crawler.getProgress();
    }

    public void startSearch() throws SearchException {                                                                  // TODO: Javadoc
        if (isSearchInProgress()) {
            throw new SearchException("A search is already in progress");
        }
        if (isSearchDone()) {
            throw new SearchException("Search is already complete. Cannot re-use Duplicate Finder object.");
        }
        List<File> allFiles;
        try {
            allFiles = allFilesFuture.get();                                                                            // blocks until Future is ready
        } catch (Exception e) {
            throw new SearchException("Failed to read all files from Future", e);
        }
        setSearchInProgress();
        this.duplicatesFuture = findDuplicates(allFiles);
    }

    /**
     * Stops an ongoing search and halts all asynchronous tasks.
     * @throws SearchException if there's an error stopping the search, or no search is in progress
     */
    public abstract void stopSearch() throws SearchException;                                                           // TODO: Set appropriate flags

    /**
     * Get the status of the current search.
     * @return Progress of the current search.
     * @throws SearchException if no search is in progress
     */
    public abstract Progress getSearchProgress() throws SearchException;

    /**
     * Get results from the search.
     * @return a 2-D list where each inner list is a collection of File objects that are suspected duplicatesFuture
     * @throws SearchException if search is still ongoing or hasn't started
     */
    public List<List<File>> getResults() throws SearchException {
        if (!isSearchDone()) {
            throw new SearchException("Search is still in progress. Cannot return results.");
        }
        List<List<File>> results;
        try {
            results = duplicatesFuture.get();
        } catch (Exception e) {
            throw new SearchException("Error reading duplicates future object", e);
        }
        return results;
    }

    /**
     * Asynchronously finds and returns duplicate files. Calls ``setSearchDone()`` when completed.
     */
    protected abstract Future<List<List<File>>> findDuplicates(List<File> allFiles);

    /**
     * @return true if search is complete, else false
     */
    public boolean isSearchDone() {
        if (!searchDone) {
            if (duplicatesFuture != null) {
                searchDone = duplicatesFuture.isDone();
            }
        }
        return searchDone;
    }

    /**
     * @return true if search is in progress
     */
    private boolean isSearchInProgress() {
        return searchInProgress;
    }

    private void setSearchInProgress() {
        this.startTime = System.currentTimeMillis();
        this.searchInProgress = true;
    }

    void setSearchDone() {
        this.searchInProgress = false;
        this.searchDone = true;
    }
}
