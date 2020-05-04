package testFiles;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class TestFiles {

    public static String DIR_WITH_RANDOM_FILES = "test/testfiles/randomFiles";
    public static List<File> DIR_WITH_RANDOM_FILES_LIST = Arrays.asList(
            new File(DIR_WITH_RANDOM_FILES + "/1.jpg"),
            new File(DIR_WITH_RANDOM_FILES + "/2.jpg"),
            new File(DIR_WITH_RANDOM_FILES + "/3.png"),
            new File(DIR_WITH_RANDOM_FILES + "/folder1")
    );
}
