package model.searchModel.searchStrategies;

import model.util.Progress;
import model.util.ScanException;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

public interface ISearchStrategy {

    /**
     * Get the status of the current search.
     * @return Progress of the current search.
     * @throws ScanException if no search is in progress
     */
    public Progress getProgress() throws ScanException;

    public Future<List<List<File>>> findDuplicates(List<File> allFiles);                                                // TODO: javadoc
}
