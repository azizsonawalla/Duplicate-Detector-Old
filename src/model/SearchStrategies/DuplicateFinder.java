package model.SearchStrategies;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public abstract class DuplicateFinder {

    private final String rootDirectory; // Root directory with duplicates
    private List<String> allFilePaths;  // Absolute paths for all files in Root directory

    public DuplicateFinder(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    /**
     * Start the search for duplicates in the root folder. This method uses background threads to search for duplicates
     * and will return immediately after triggering the search. To get progress stats for the search, call
     * ``getProgress()`` and to interrupt the search call ``stopSearch()``
     * @throws SearchException if there's an error starting the search
     */
    public abstract void startSearch() throws SearchException;

    /**
     * Stops an ongoing search and halts all background threads. Does nothing if no search is in progress.
     * @throws SearchException if there's an error stopping the search
     */
    public abstract void stopSearch() throws SearchException;

    /**
     * Get the status of the current search.
     * @return Progress of the current search. Null if no search is currently running
     */
    public abstract Progress getProgress();

    /**
     * Expands all file paths under this.rootDirectory to absolute paths and save it to this.allFilePaths.
     * Also filters out files with unsupported file extensions (see ``config.Config``)
     */
    private void loadFilePaths() {
        // TODO
        throw new NotImplementedException();
    }
}
