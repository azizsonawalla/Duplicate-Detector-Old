package model.searchStrategies;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testFiles.TestFiles;
import testUtils.SearchResultComparator;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class MetadataDuplicateFinderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void sampleTest() throws InterruptedException {
        MetadataDuplicateFinder mdf = new MetadataDuplicateFinder(TestFiles.EXACT_DUPS_DIR.getPath());
        mdf.startSearch();

        List<List<File>> expected = TestFiles.EXACT_DUPS_DIR_DUPLICATE_SETS;
        while(!mdf.isSearchDone());                                                                                     // TODO: add timeout

        assertTrue(SearchResultComparator.fileResultsAreEqual(mdf.getResults(), expected));
    }
}