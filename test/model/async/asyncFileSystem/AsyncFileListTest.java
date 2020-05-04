package model.async.asyncFileSystem;


import model.async.threadPool.AppThreadPool;
import org.junit.Test;
import testFiles.TestFiles;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AsyncFileListTest {

    @Test
    public void testCall() throws ExecutionException, InterruptedException {
        File testFile = new File(TestFiles.DIR_WITH_RANDOM_FILES);
        AsyncFileList afl = new AsyncFileList(testFile);

        Future<File[]> listFuture = AppThreadPool.getInstance().submit(afl);
        List<File> expectedList = TestFiles.DIR_WITH_RANDOM_FILES_LIST;
        File[] list = listFuture.get();

        assertEquals(list.length, expectedList.size());
        for (File returnedFile: list) {
            assertTrue(expectedList.contains(returnedFile));
        }
    }
}