package util;

import javafx.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Util methods to interact with the file system
 */
public class FileSystemUtil {

    private static Logger log = new Logger(FileSystemUtil.class);

    /**
     * Get the extension of a file
     * @param file a file with an extension
     * @return the extension associated with the file. Empty string if file has no extension.
     */
    public static String getFileExtension(File file) {
        return splitFileName(file).getValue();
    }

    /**
     * Split the file name into its basename and extension
     * @param file a file with an extension
     * @return a pair with the basename and extension (Pair<basename, ext>).
     * If no extension is associated with the file, returns empty string in the value part of the pair.
     */
    static Pair<String, String> splitFileName(File file) {
        String name = file.getName();
        String[] parts = name.split("\\.");
        if (parts.length > 1) {
            return new Pair<>(parts[0], parts[1]);
        }
        if (parts.length == 1) {
            return new Pair<>(parts[0], "");
        }
        return new Pair<>("", "");
    }

    /**
     * Copies data from the source file to the destination file
     * @param src file to copy from
     * @param dest file to copy to
     * @return destination file object
     */
    static File copyFile(File src, File dest) throws IOException {
        FileOutputStream out = new FileOutputStream(dest);
        FileInputStream in = new FileInputStream(src);

        byte[] buffer = new byte[1024];
        int lengthRead;
        while ((lengthRead = in.read(buffer)) > 0) {
            out.write(buffer, 0, lengthRead);
            out.flush();
        }
        return dest;
    }

    /**
     * Create a temporary file based on a source file's name. This method *does not* copy over the data
     * @param originalFile source file
     * @return File object of the temporary file
     * @throws IOException if there is an error reading the source
     */
    static File createTempFileReference(File originalFile) throws IOException {
        Pair<String, String> nameParts = splitFileName(originalFile);
        String basename = nameParts.getKey();
        String ext = nameParts.getValue();
        File tmp =  File.createTempFile("000_" + basename, ext);
        tmp.deleteOnExit();
        return tmp;
    }

    /**
     * Delete all files in a collection. This permanently deletes files (as opposed to moving to trash/recycle bin)
     * @param files a collection of files to delete.
     * @return a map with the original File object and a boolean value - true = successfully deleted,
     * false = error while deleting
     */
    public static Map<File, Boolean> deleteFiles(List<File> files) {
        HashMap<File, Boolean> results = new HashMap<>();
        for (File file: files) {
            results.put(file, file.delete());
        }
        return results;
    }

    /**
     * Concetenate the names of a collection of files into a single string
     * @param files a collection of files
     * @return a single string with file names, delimited by a colon (;)
     */
    private static String getFileNamesAsString(List<File> files) {
        StringBuilder names = new StringBuilder();
        for (File file: files) {
            names.append(file.getName()).append("; ");
        }
        return names.toString();
    }
}
