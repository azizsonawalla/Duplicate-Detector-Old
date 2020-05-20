package model.searchModel.searchStrategies;

import util.Progress;
import util.ScanException;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Strategy Interface for a duplicate file detection strategy
 */
public interface ISearchStrategy {

    /**
     * Get the status of the current search.
     * @return Progress of the current search.
     * @throws ScanException if no search is in progress or there was an error in the search
     */
    Progress getProgress() throws ScanException;

    /**
     * Initiate search for duplicates. Returns immediately with Future object that populates asynchronously
     * @param allFiles a list of files to compare
     * @return a Future object that will be populated with search results when search completes.
     */
    Future<List<List<File>>> findDuplicates(List<File> allFiles);
}
