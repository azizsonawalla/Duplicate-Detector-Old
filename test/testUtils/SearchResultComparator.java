package testUtils;

import java.io.File;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SearchResultComparator {

    /**
     * Compares the result and expected value for results of a duplicate file search. Internal ordering of the two
     * parameters does not matter, only the contents and the grouping are considered.
     * @param result the result returned from the search
     * @param expected the expected result
     * @return true if the two parameters are the same (order-independent), false otherwise
     */
    public static boolean fileResultsAreEqual(List<List<File>> result, List<List<File>> expected) {
        if (result == null && expected == null) {
            return true;
        }
        if (result == null || expected == null) {
            return false;
        }
        if (result.size() != expected.size()) {
            return false;
        }
        return hash2DFileArray(result).equals(hash2DFileArray(expected));
    }

    /**
     * Returns an order-independent hash of a 2D array
     * @param array array to hash
     * @return order-independent hash
     */
    private static BigInteger hash2DFileArray(List<List<File>> array) {
        LinkedList<BigInteger> hashed = new LinkedList<>();
        for (List<File> innerArray: array) {
            hashed.add(hashFileArray(innerArray));
        }
        hashed.sort(BigInteger::compareTo);
        return BigInteger.valueOf(hashed.hashCode());
    }

    /**
     * Returns an order-independent hash of an array
     * @param array array to hash
     * @return order-independent hash
     */
    private static BigInteger hashFileArray(List<File> array) {
        array.sort(Comparator.comparing(File::getPath));
        return BigInteger.valueOf(array.hashCode());
    }
}
