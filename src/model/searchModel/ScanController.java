package model.searchModel;

import config.Config;
import model.async.asyncFileSystem.AsyncDirectoryCrawler;
import model.async.threadPool.AppThreadPool;
import model.searchModel.searchStrategies.ISearchStrategy;
import model.util.Progress;
import model.util.ScanException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

/**
 * A controller to manage file scan related tasks. This is a 'use-and-throw' object - once it has been used to perform
 * a scan, or if any of the stages are stopped/error-out midway, the scan cannot be restarted. Create a new instance
 * of the controller instead to restart a scan. Some search strategies may use caching mechanisms and so all progress
 * may not be lost.
 */
public class ScanController {

    /**
     * Constants to track current stage of the search
     */
    private enum ScanStage {
        NOT_STARTED, PRE_SEARCH_IN_PROGRESS, PRE_SEARCH_DONE, SEARCH_IN_PROGRESS, SEARCH_DONE, STOPPED, ERROR
    }
    private ScanStage currentStage = ScanStage.NOT_STARTED;

    /* Pre-search objects */
    private List<File> rootDirectories;
    private AsyncDirectoryCrawler crawler;                                                                              // Async crawler for allFiles
    private Future<List<File>> allFilesFuture;                                                                          // Future object for allFiles

    /* Search objects */
    private Future<List<List<File>>> duplicatesFuture;                                                                  // Future object for sets of duplicate files
    private ISearchStrategy strategy;


    /**
     * A controller to manage scan related tasks.
     * @param rootDirectories the list of directories to scan
     * @param strategy strategy for finding duplicates
     */
    public ScanController(List<File> rootDirectories, ISearchStrategy strategy) {
        this.rootDirectories = rootDirectories;
        this.strategy = strategy;
    }

    /**
     * Get information on the current scan stage and progress statistics
     * @throws ScanException if no task is being performed
     */
    public Progress getProgress() throws ScanException {
        if (isPreSearchInProgress() || isPreSearchDone()) {
            return this.getPreSearchProgress();
        }
        if (isSearchInProgress() || isSearchDone()) {
            return this.getSearchProgress();
        }
        throw new ScanException("No stage is currently being executed");
    }

    /**
     * Executes all stages of the scan asynchronously (returns immediately)
     * @throws ScanException if there is a problem during any stage of the scan
     */
    public void start() throws ScanException {                                                                          // TODO: implement this
        throw new NotImplementedException();
    }

    /**
     * Execute the pre-search stage asynchronously (returns immediately).
     * Must be called before starting the search stage.
     * @throws ScanException if there is a problem starting the pre-search stage
     */
    public void startPreSearch() throws ScanException {
        if (currentStage != ScanStage.NOT_STARTED) {
            throw new ScanException("A scan has already started. Cannot start pre-search stage");
        }
        this.crawler = new AsyncDirectoryCrawler(this.rootDirectories, Config.SUPPORTED_FILE_TYPES);
        this.allFilesFuture = AppThreadPool.getInstance().submit(this.crawler);
        setCurrentStage(ScanStage.PRE_SEARCH_IN_PROGRESS);
    }

    /**
     * Execute the search stage asynchronously (returns immediately).
     * Must be called after completing the pre-search stage.
     * If pre-search still in progress, will wait for it to complete.
     * @throws ScanException if there is a problem during the search stage, or pre-search hasn't completed.
     */
    public void startSearch() throws ScanException {
        if (!isPreSearchDone()) {
            throw new ScanException("Cannot start search stage. Pre-search hasn't started or search has already started.");
        }
        List<File> allFiles;
        try {
            allFiles = allFilesFuture.get();                                                                            // blocks until Future is ready
        } catch (Exception e) {
            setCurrentStage(ScanStage.ERROR);
            throw new ScanException("Failed to read all files from Future", e);
        }
        setCurrentStage(ScanStage.SEARCH_IN_PROGRESS);
        this.duplicatesFuture = this.strategy.findDuplicates(allFiles);
    }

    /**
     * Stops the current stage of the scan
     * @throws ScanException if there's an error while stopping, or no stage is in progress
     */
    public void stop() throws ScanException {
        if (isPreSearchInProgress()) {
            this.stopPreSearch();
        }
        if (isSearchInProgress()) {
            this.stopSearch();
        }
        throw new ScanException("No stage is currently being executed");
    }

    /**
     * Get results from the scan. Can only be called once search stage is complete.
     * @return a 2-D list where each inner list is a collection of File objects that are suspected duplicates
     * @throws ScanException if the search stage has not completed or there is an error retrieving results
     */
    public List<List<File>> getResults() throws ScanException {                                                         // TODO: create new class called SearchResults
        if (!isSearchDone()) {
            throw new ScanException("Search stage not complete. Cannot return results.");
        }
        List<List<File>> results;
        try {
            results = duplicatesFuture.get();
        } catch (Exception e) {
            setCurrentStage(ScanStage.ERROR);
            throw new ScanException("Error reading duplicates future object", e);
        }
        return results;
    }

    /**
     * @return true if pre-search stage is in progress, else false
     */
    public boolean isPreSearchInProgress() {
        refreshCurrentStage();
        return currentStage == ScanStage.PRE_SEARCH_IN_PROGRESS;
    }

    /**
     * @return true if pre-search stage is done, else false
     */
    public boolean isPreSearchDone() {
        refreshCurrentStage();
        return currentStage == ScanStage.PRE_SEARCH_DONE;
    }

    /**
     * @return true if search stage is in progress, else false
     */
    public boolean isSearchInProgress() {
        refreshCurrentStage();
        return currentStage == ScanStage.SEARCH_IN_PROGRESS;
    }

    /**
     * @return true if the search stage is complete, else false
     */
    public boolean isSearchDone() {
        refreshCurrentStage();
        return currentStage == ScanStage.SEARCH_DONE;
    }


    /**
     * Stop the pre-search stage
     * @throws ScanException if pre-search stage is not currently in progress, or there is a problem stopping it
     */
    private void stopPreSearch() throws ScanException {
        if (!isPreSearchInProgress()) {
            throw new ScanException("Pre-search stage is not currently in progress. Cannot stop it.");
        }
        try {
            this.crawler.cancel();
            setCurrentStage(ScanStage.STOPPED);
        } catch (IOException e) {
            setCurrentStage(ScanStage.ERROR);
            throw new ScanException("Error while trying to stop pre-search stage", e);
        }
    }

    /**
     * Stop the search stage
     * @throws ScanException if search stage is not currently in progress, or there is a problem stopping it
     */
    private void stopSearch() throws ScanException {
        if (!isSearchInProgress()) {
            throw new ScanException("Search stage is not currently in progress. Cannot stop it.");
        }
        try {
            this.duplicatesFuture.cancel(true);
            setCurrentStage(ScanStage.STOPPED);
        } catch (Exception e) {
            setCurrentStage(ScanStage.ERROR);
            throw new ScanException("Error while trying to stop Search stage", e);
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
     * Refreshes the current stage flag. Only updates to PRE_SEARCH_DONE and SEARCH_DONE stages (i.e. stages that run
     * asynchronously). Other stages are set synchronously by other methods.
     */
    private void refreshCurrentStage() {                                                                                // TODO: replace with a more elegant solution. This may break if start method impl. changes.
        switch (currentStage) {
            case PRE_SEARCH_IN_PROGRESS:
                if (this.allFilesFuture != null && allFilesFuture.isDone()) {
                    this.currentStage = ScanStage.PRE_SEARCH_DONE;
                }
                break;
            case SEARCH_IN_PROGRESS:
                if (this.duplicatesFuture != null && duplicatesFuture.isDone()) {
                    this.currentStage = ScanStage.SEARCH_DONE;
                }
                break;
        }
    }
}
