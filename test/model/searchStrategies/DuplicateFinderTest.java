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

public class DuplicateFinderTest {

    class SampleDuplicateFinder extends DuplicateFinder {                                                               // TODO: javadoc

        public SampleDuplicateFinder(String rootDirectory) {
            super(rootDirectory);
        }

        @Override
        public void stopSearch() throws SearchException {
            this.setSearchDone();
        }

        @Override
        public Progress getSearchProgress() throws SearchException {
            return null;
        }

        @Override
        protected Future<List<List<File>>> findDuplicates(List<File> allFiles) {
            return null;
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void sampleTest() {
        assertTrue(true);
    }
}