package model.searchModel.searchStrategies;

import model.searchModel.SearchController;
import org.junit.Test;
import testFiles.TestFiles;
import testUtils.SearchResultComparator;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class MetadataStrategyTest {

    @Test
    public void sampleTest() throws InterruptedException {
        MetadataStrategy mdf = new MetadataStrategy();
        SearchController controller = new SearchController(TestFiles.EXACT_DUPS_DIR, mdf);
        controller.startSearch();

        List<List<File>> expected = TestFiles.EXACT_DUPS_DIR_DUPLICATE_SETS;
        while(!controller.isSearchDone());                                                                                     // TODO: add timeout

        assertTrue(SearchResultComparator.fileResultsAreEqual(controller.getResults(), expected));
    }
}