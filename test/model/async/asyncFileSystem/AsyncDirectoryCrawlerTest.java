package model.async.asyncFileSystem;

import model.async.threadPool.AppThreadPool;
import org.junit.Test;
import testFiles.TestFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AsyncDirectoryCrawlerTest {

    @Test
    public void testCall_Depth2_noExtensionFilter() throws ExecutionException, InterruptedException {
        List<String> extensions = new ArrayList<>();
        AsyncDirectoryCrawler adc = new AsyncDirectoryCrawler(TestFiles.DEPTH_2_DIR, extensions);
        Future<List<File>> listFuture = AppThreadPool.getInstance().submit(adc);
        List<File> list = listFuture.get();
        List<File> expectedList = TestFiles.DEPTH_2_DIR_LIST_FILES;

        assertEquals(list.size(), expectedList.size());
        for (File returnedFile: list) {
            assertTrue(expectedList.contains(returnedFile));
        }
    }

    // @Test
    public void testGetProgress() {
        // TODO
    }
}