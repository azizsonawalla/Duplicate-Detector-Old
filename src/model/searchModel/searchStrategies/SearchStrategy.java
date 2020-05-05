package model.searchModel.searchStrategies;

import model.util.Progress;
import model.util.SearchException;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

public interface SearchStrategy {

    /**
     * Get the status of the current search.
     * @return Progress of the current search.
     * @throws SearchException if no search is in progress
     */
    public Progress getProgress() throws SearchException;

    public Future<List<List<File>>> findDuplicates(List<File> allFiles);                                                // TODO: javadoc
}
