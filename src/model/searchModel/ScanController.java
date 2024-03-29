package model.searchModel;

import config.Config;
import model.async.asyncFileSystem.AsyncDirectoryCrawler;
import model.async.threadPool.AppThreadPool;
import model.searchModel.searchStrategies.ISearchStrategy;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.Progress;
import util.ScanException;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * A controller to manage file scan related tasks.
 *
 * This is a single-use object - once a scan is complete, or if any of the stages are stopped/error-out midway, the scan
 * cannot be restarted. Create a new instance of the controller instead to restart a scan. Some search strategies may
 * use caching mechanisms and so all progress may not be lost.
 */
public class ScanController {

    /**
     * Constants to track current stage of the search
     */
    private enum ScanStage {
        NOT_STARTED,
        PRE_SEARCH_IN_PROGRESS,
        PRE_SEARCH_DONE,
        SEARCH_IN_PROGRESS,
        SEARCH_DONE,
        STOPPED,
        ERRORED
    }
    private ScanStage currentStage = ScanStage.NOT_STARTED;

    private List<Exception> errors;

    /* Pre-search objects */
    private List<File> rootDirectories;
    private AsyncDirectoryCrawler crawler;                                                                              // Async crawler for allFiles
    private Future<List<File>> allFilesFuture;                                                                          // Future object for allFiles
    private List<File> allFiles;                                                                                        // Final pre search results

    /* Search objects */
    private Future<List<List<File>>> duplicatesFuture;                                                                  // Future object for sets of duplicate files
    private List<List<File>> duplicates;                                                                                // Final search results
    private ISearchStrategy strategy;


    /**
     * A controller to manage scan related tasks.
     * @param rootDirectories the list of directories to scan
     * @param strategy strategy for finding duplicates
     */
    public ScanController(List<File> rootDirectories, ISearchStrategy strategy) {
        this.rootDirectories = rootDirectories;
        this.strategy = strategy;
        this.errors = new LinkedList<>();
    }

    /**
     * A controller to manage scan related tasks.
     * @param rootDirectory the directory to scan
     */
    public ScanController(File rootDirectory) {
        this(Arrays.asList(rootDirectory), null);
    }

    /**
     * Set the search strategy
     * @param strategy search strategy to be applied
     */
    public void setStrategy(ISearchStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Get the applied search strategy. May be null if no strategy is set.
     * @return currently applied search strategy (may be null)
     */
    public ISearchStrategy getStrategy() {
        return strategy;
    }

    /**
     * Get information on the current scan stage and progress statistics
     * @throws ScanException if no task is being performed
     */
    public Progress getProgress() throws ScanException {
        if (isPreSearchInProgress()) {
            return this.getPreSearchProgress();
        }
        if (isPreSearchDone()) {
            return getPreSearchDoneProgressObject();
        }
        if (isSearchInProgress()) {
            return this.getSearchProgress();
        }
        if (isSearchDone()) {
            return getSearchDoneProgressObject();
        }
        throw new ScanException("No stage is currently being executed");
    }

    /**
     * Executes all stages of the scan asynchronously (returns immediately)
     * @throws ScanException if there is a problem during any stage of the scan
     */
    public void start() throws ScanException {
        throw new NotImplementedException();
    }

    /**
     * Execute the pre-search stage asynchronously (returns immediately).
     * Must be called before starting the search stage.
     * @throws ScanException if there is a problem starting the pre-search stage
     */
    public boolean startPreSearch() throws ScanException {
        if (getCurrentStage() != ScanStage.NOT_STARTED) {
            throw new ScanException("A scan has already started. Cannot start pre-search stage");
        }
        this.crawler = new AsyncDirectoryCrawler(this.rootDirectories, Config.SUPPORTED_FILE_TYPES);
        this.allFilesFuture = AppThreadPool.getInstance().submit(this.crawler);
        setCurrentStage(ScanStage.PRE_SEARCH_IN_PROGRESS);
        return true;
    }

    /**
     * Execute the search stage asynchronously (returns immediately).
     * Must be called after completing the pre-search stage.
     * @throws ScanException if there is a problem during the search stage, or pre-search hasn't completed.
     */
    public void startSearch() throws ScanException {
        if (!isPreSearchDone()) {
            throw new ScanException("Cannot start search stage. Pre-search hasn't started or search has already started.");
        }
        if (strategy == null) {
            throw new ScanException("No strategy set for search");
        }
        setCurrentStage(ScanStage.SEARCH_IN_PROGRESS);
        this.duplicatesFuture = this.strategy.findDuplicates(getPreSearchResults());
    }

    /**
     * Stops the current stage of the scan
     * @throws ScanException if there's an error while stopping, or no stage is in progress
     */
    public boolean stop() throws ScanException {
        if (isPreSearchInProgress()) {
            return this.stopPreSearch();
        }
        if (isSearchInProgress()) {
            return this.stopSearch();
        }
        throw new ScanException("No stage is currently being executed");
    }

    /**
     * Get results from the scan. Can only be called once search stage is complete.
     * @return a 2-D list where each inner list is a collection of File objects that are suspected duplicates
     * @throws ScanException if the search stage has not completed or there is an error retrieving results
     */
    public List<List<File>> getResults() throws ScanException {
        if (!isSearchDone()) {
            throw new ScanException("Search stage not complete. Cannot return results.");
        }
        return getSearchResults();
    }

    /**
     * @return true if pre-search stage is in progress, else false
     */
    public boolean isPreSearchInProgress() {
        refreshCurrentStage();
        return getCurrentStage() == ScanStage.PRE_SEARCH_IN_PROGRESS;
    }

    /**
     * @return true if pre-search stage is done, else false
     */
    public boolean isPreSearchDone() {
        refreshCurrentStage();
        return getCurrentStage() == ScanStage.PRE_SEARCH_DONE;
    }

    /**
     * @return true if search stage is in progress, else false
     */
    public boolean isSearchInProgress() {
        refreshCurrentStage();
        return getCurrentStage() == ScanStage.SEARCH_IN_PROGRESS;
    }

    /**
     * @return true if the search stage is complete, else false
     */
    public boolean isSearchDone() {
        refreshCurrentStage();
        return getCurrentStage() == ScanStage.SEARCH_DONE;
    }

    /**
     * @return the root directories that are being searched
     */
    public List<File> getRootDirectories() {
        return rootDirectories;
    }


    /**
     * Creates Progress object for SEARCH_DONE stage
     * @return Progress object for SEARCH_DONE stage
     */
    private Progress getSearchDoneProgressObject() {
        long done = getPreSearchResults().size();
        long positives = getSearchResults().size();
        return new Progress(done, 0, 0, positives, 0, errors, getCurrentStage().toString());
    }

    /**
     * Creates Progress object for PRE_SEARCH_DONE stage
     * @return Progress object for PRE_SEARCH_DONE stage
     */
    private Progress getPreSearchDoneProgressObject() {
        long done = getPreSearchResults().size();
        long positives = 0;
        return new Progress(done, 0, 0, positives, 0, errors, getCurrentStage().toString());
    }

    /**
     * Stop the pre-search stage
     * @throws ScanException if pre-search stage is not currently in progress, or there is a problem stopping it
     */
    private boolean stopPreSearch() throws ScanException {
        if (!isPreSearchInProgress()) {
            throw new ScanException("Pre-search stage is not currently in progress. Cannot stop it.");
        }
        this.crawler.cancel();
        setCurrentStage(ScanStage.STOPPED);
        return true;
    }

    /**
     * Stop the search stage
     * @throws ScanException if search stage is not currently in progress, or there is a problem stopping it
     */
    private boolean stopSearch() throws ScanException {
        if (!isSearchInProgress()) {
            throw new ScanException("Search stage is not currently in progress. Cannot stop it.");
        }
        try {
            this.duplicatesFuture.cancel(true);
            setCurrentStage(ScanStage.STOPPED);
            return true;
        } catch (Exception e) {
            setCurrentStage(ScanStage.ERRORED);
            ScanException scanException = new ScanException("Error while trying to stop Search stage", e);
            errors.add(scanException);
            throw scanException;
        }
    }

    /**
     * Get the progress on pre-search stage
     * @return Progress object with current pre-search stats
     * @throws ScanException if pre-search stage has not started
     */
    private Progress getPreSearchProgress() throws ScanException {
        if (!isPreSearchInProgress()) {
            throw new ScanException("Pre-search stage is not currently in progress.");
        }
        return crawler.getProgress();
    }

    /**
     * Get the progress on search stage
     * @return Progress object with current pre-search stats
     * @throws ScanException if search stage has not started
     */
    private Progress getSearchProgress() throws ScanException {
        if (!isSearchInProgress()) {
            throw new ScanException("No search currently running. Cannot return progress.");
        }
        return this.strategy.getProgress();
    }

    /**
     * Sets the current stage to the given stage
     * @param stage new stage
     */
    private void setCurrentStage(ScanStage stage) {
        this.currentStage = stage;
    }

    /**
     * @return the current stage
     */
    private ScanStage getCurrentStage() {
        return this.currentStage;
    }

    /**
     * Refresh the current stage and extract results if ready
     */
    private void refreshCurrentStage() {
        if (getCurrentStage() == ScanStage.PRE_SEARCH_IN_PROGRESS
                && this.allFilesFuture != null && allFilesFuture.isDone()) {
            setCurrentStage(ScanStage.PRE_SEARCH_DONE);
            getPreSearchResults();
            return;
        }

        if (getCurrentStage() == ScanStage.SEARCH_IN_PROGRESS
                && this.duplicatesFuture != null && duplicatesFuture.isDone()) {
            setCurrentStage(ScanStage.SEARCH_DONE);
            getSearchResults();
        }
    }

    /**
     * Extract pre search results
     */
    private List<File> getPreSearchResults() {
        if (this.allFiles == null) {
            try {
                this.allFiles = allFilesFuture.get();
            } catch (Exception e) {
                setCurrentStage(ScanStage.ERRORED);
                errors.add(new ScanException("Failed to get pre search results", e));
            }
        }
        return this.allFiles;
    }

    /**
     * Extract search results
     */
    private List<List<File>> getSearchResults() {
        if (this.duplicates == null) {
            try {
                this.duplicates = duplicatesFuture.get();
            } catch (Exception e) {
                setCurrentStage(ScanStage.ERRORED);
                errors.add(new ScanException("Failed to get search results", e));
            }
        }
        return this.duplicates;
    }
}
