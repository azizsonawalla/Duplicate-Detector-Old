package model.searchStrategies;

import model.util.Progress;
import model.util.SearchException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

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
        MetadataDuplicateFinder mdf = new MetadataDuplicateFinder("test/testFiles/allDuplicates");
        mdf.startSearch();
        Thread.sleep(10000);
        System.out.println(mdf.getResults().toString());
    }
}