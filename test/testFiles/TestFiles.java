package testFiles;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class TestFiles {

    private static String TEST_FILES_PATH = "test/testfiles";

    private static String DEPTH_1_DIR_PATH = TEST_FILES_PATH + "/depth1";
    public static File DEPTH_1_DIR = new File(DEPTH_1_DIR_PATH);
    public static List<File> DEPTH_1_DIR_LIST = Arrays.asList(
            new File(DEPTH_1_DIR_PATH + "/1.jpg"),
            new File(DEPTH_1_DIR_PATH + "/2.jpg"),
            new File(DEPTH_1_DIR_PATH + "/3.png"),
            new File(DEPTH_1_DIR_PATH + "/folder1")
    );

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
}
