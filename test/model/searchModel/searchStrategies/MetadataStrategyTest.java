package model.searchModel.searchStrategies;

import model.searchModel.ScanController;
import org.junit.Test;
import testFiles.TestFiles;
import testUtils.SearchResultComparator;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class MetadataStrategyTest {

    @Test
    public void sampleTest() {
        MetadataStrategy mdf = new MetadataStrategy();
        ScanController controller = new ScanController(Arrays.asList(TestFiles.EXACT_DUPS_DIR), mdf);
        controller.startPreSearch();
        while(!controller.isPreSearchDone());                                                                           // TODO: add timeout

        controller.startSearch();
        List<List<File>> expected = TestFiles.EXACT_DUPS_DIR_DUPLICATE_SETS;
        while(!controller.isSearchDone());                                                                              // TODO: add timeout

        assertTrue(SearchResultComparator.fileResultsAreEqual(controller.getResults(), expected));
    }
}