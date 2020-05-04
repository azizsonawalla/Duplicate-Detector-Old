package testFiles;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * A constants class to manage/store test file references and their file structure, to make it easier to move files
 * around without having to change all the test code.
 */
public class TestFiles {

    private static String TEST_FILES_PATH = "test/testfiles";

    /* A folder with directory tree of depth 1 */
    private static String DEPTH_1_DIR_PATH = TEST_FILES_PATH + "/depth1";
    public static File DEPTH_1_DIR = new File(DEPTH_1_DIR_PATH);
    public static List<File> DEPTH_1_DIR_LIST = Arrays.asList(
            new File(DEPTH_1_DIR_PATH + "/1.jpg"),
            new File(DEPTH_1_DIR_PATH + "/2.jpg"),
            new File(DEPTH_1_DIR_PATH + "/3.png"),
            new File(DEPTH_1_DIR_PATH + "/folder1")
    );

    /* A folder with directory tree of depth 2 */
    private static String DEPTH_2_DIR_PATH = TEST_FILES_PATH + "/depth2";
    public static File DEPTH_2_DIR = new File(DEPTH_2_DIR_PATH);
    public static List<File> DEPTH_2_DIR_LIST_FILES = Arrays.asList(
            new File(DEPTH_2_DIR_PATH + "/1.jpg"),
            new File(DEPTH_2_DIR_PATH + "/2.jpg"),
            new File(DEPTH_2_DIR_PATH + "/3.png"),
            new File(DEPTH_2_DIR_PATH + "/folder1/1.jpg"),
            new File(DEPTH_2_DIR_PATH + "/folder1/2.jpg"),
            new File(DEPTH_2_DIR_PATH + "/folder1/3.png")
    );
    public static List<File> DEPTH_2_DIR_LIST_FOLDERS = Arrays.asList(
            new File(DEPTH_2_DIR_PATH + "/folder1")
    );

    /* A folder with depth 3 and exact duplicates */
    private static String EXACT_DUPS_DIR_PATH = TEST_FILES_PATH + "/depth3_withExactDuplicates";
    public static File EXACT_DUPS_DIR = new File(EXACT_DUPS_DIR_PATH);
    public static List<File> EXACT_DUPS_DIR_LIST_FILES = Arrays.asList(
            new File(EXACT_DUPS_DIR_PATH + "/1.jpg"),
            new File(EXACT_DUPS_DIR_PATH + "/2.jpg"),
            new File(EXACT_DUPS_DIR_PATH + "/3.png"),
            new File(EXACT_DUPS_DIR_PATH + "/folder1/1.jpg"),
            new File(EXACT_DUPS_DIR_PATH + "/folder1/2.jpg"),
            new File(EXACT_DUPS_DIR_PATH + "/folder1/3.png"),
            new File(EXACT_DUPS_DIR_PATH + "/folder1/folder1/1.jpg"),
            new File(EXACT_DUPS_DIR_PATH + "/folder1/folder1/2.jpg"),
            new File(EXACT_DUPS_DIR_PATH + "/folder1/folder1/3.png"),
            new File(EXACT_DUPS_DIR_PATH + "/folder2/1.jpg"),
            new File(EXACT_DUPS_DIR_PATH + "/folder2/2.jpg"),
            new File(EXACT_DUPS_DIR_PATH + "/folder2/3.png")
    );
    public static List<File> EXACT_DUPS_DIR_LIST_FOLDERS = Arrays.asList(
            new File(EXACT_DUPS_DIR_PATH + "/folder1"),
            new File(EXACT_DUPS_DIR_PATH + "/folder1/folder1"),
            new File(EXACT_DUPS_DIR_PATH + "/folder2")
    );
    public static List<List<File>> EXACT_DUPS_DIR_DUPLICATE_SETS = Arrays.asList(
            Arrays.asList(
                    new File(EXACT_DUPS_DIR_PATH + "/1.jpg"),
                    new File(EXACT_DUPS_DIR_PATH + "/folder1/1.jpg"),
                    new File(EXACT_DUPS_DIR_PATH + "/folder1/folder1/1.jpg"),
                    new File(EXACT_DUPS_DIR_PATH + "/folder2/1.jpg")
            ),
            Arrays.asList(
                    new File(EXACT_DUPS_DIR_PATH + "/2.jpg"),
                    new File(EXACT_DUPS_DIR_PATH + "/folder1/2.jpg"),
                    new File(EXACT_DUPS_DIR_PATH + "/folder1/folder1/2.jpg"),
                    new File(EXACT_DUPS_DIR_PATH + "/folder2/2.jpg")
            ),
            Arrays.asList(
                    new File(EXACT_DUPS_DIR_PATH + "/3.png"),
                    new File(EXACT_DUPS_DIR_PATH + "/folder1/3.png"),
                    new File(EXACT_DUPS_DIR_PATH + "/folder1/folder1/3.png"),
                    new File(EXACT_DUPS_DIR_PATH + "/folder2/3.png")
            )
    );
}
