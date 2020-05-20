package model.util;

import javafx.util.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;

public class FileSystemUtil {

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
}
