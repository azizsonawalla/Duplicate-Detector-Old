package model.searchModel;

import config.Config;
import model.async.asyncFileSystem.AsyncDirectoryCrawler;
import model.async.threadPool.AppThreadPool;
import model.searchModel.searchStrategies.SearchStrategy;
import model.util.Progress;
import model.util.SearchException;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

/**
 * An abstract strategy class for finding duplicate files. Contains common methods for running file comparison search.
 */
public class SearchController {

    /* Flags */
    private boolean searchInProgress = false;
    private boolean searchDone = false;                                                                                 // TODO: add flags for pre-search

    /* Pre-search objects */
    private AsyncDirectoryCrawler crawler;                                                                              // Async crawler for allFiles
    private Future<List<File>> allFilesFuture;                                                                          // Future object for allFiles

    /* Search objects */
    private Future<List<List<File>>> duplicatesFuture;                                                                  // Future object for sets of duplicate files
    private SearchStrategy strategy;


    public SearchController(File rootDirectory, SearchStrategy strategy) {                                              // TODO: Add the ability to pass multiple roots
        this.crawler = new AsyncDirectoryCrawler(rootDirectory, Config.SUPPORTED_FILE_TYPES);
        this.allFilesFuture = AppThreadPool.getInstance().submit(crawler);                                              // TODO: move to new method startPreSearch()
        this.strategy = strategy;
    }

    /**
     * Get the progress on pre-search tasks
     * @return Progress object with current pre-search stats
     * @throws SearchException if search has already started or completed
     */
    public Progress getPreSearchProgress() throws SearchException {
        if (isSearchInProgress() || isSearchDone()) {                                                                   // TODO: add presearch flag
            throw new SearchException("Search has already started.");
        }
        return crawler.getProgress();                                                                                   // TODO: might not need to have check if this method does check
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
        this.duplicatesFuture = this.strategy.findDuplicates(allFiles);
    }

    public Progress getSearchProgress() throws SearchException {
        if (!isSearchInProgress()) {
            throw new SearchException("No search currently running. Cannot return progress.");
        }
        return this.strategy.getProgress();                                                                             // TODO: might not need to have check if this method does check
    }

    /**
     * Stops an ongoing search and halts all asynchronous tasks.
     * @throws SearchException if there's an error stopping the search, or no search is in progress
     */
    public void stopSearch() throws SearchException {
        this.duplicatesFuture.cancel(true);                                                          // TODO: check if search is running / duplicates future !=null
        setSearchDone();                                                                                                // TODO: search done != search interrupted. Add different flag
    }

    /**
     * Get results from the search.
     * @return a 2-D list where each inner list is a collection of File objects that are suspected duplicatesFuture
     * @throws SearchException if search is still ongoing or hasn't started
     */
    public List<List<File>> getResults() throws SearchException {                                                       // TODO: create new class called SearchResults
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
     * @return true if search is complete, else false
     */
    public boolean isSearchDone() {
        if (!searchDone) {
            if (duplicatesFuture != null && duplicatesFuture.isDone()) {
                setSearchDone();
            }
        }
        return searchDone;
    }

    /**
     * @return true if search is in progress
     */
    public boolean isSearchInProgress() {
        if (searchInProgress) {
            if (duplicatesFuture != null && duplicatesFuture.isDone()) {
                setSearchDone();
            }
        }
        return searchInProgress;
    }

    private void setSearchInProgress() {
        this.searchInProgress = true;
    }

    private void setSearchDone() {
        this.searchInProgress = false;
        this.searchDone = true;
    }
}
