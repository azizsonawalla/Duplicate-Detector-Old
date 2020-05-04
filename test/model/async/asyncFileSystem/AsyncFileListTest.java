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
    public void testCall_SubFilesAndFolders() throws ExecutionException, InterruptedException {
        File testFile = TestFiles.DEPTH_1_DIR;
        AsyncFileList afl = new AsyncFileList(testFile);

        Future<File[]> listFuture = AppThreadPool.getInstance().submit(afl);
        List<File> expectedList = TestFiles.DEPTH_1_DIR_LIST;
        File[] list = listFuture.get();

        assertEquals(list.length, expectedList.size());
        for (File returnedFile: list) {
            assertTrue(expectedList.contains(returnedFile));
        }
    }

    @Test
    public void testCall_EmptyFolder() throws ExecutionException, InterruptedException {
        // TODO
    }

    @Test
    public void testCall_NotAFolder() throws ExecutionException, InterruptedException {
        // TODO
    }
}