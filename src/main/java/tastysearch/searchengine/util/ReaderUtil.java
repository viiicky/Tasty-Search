package tastysearch.searchengine.util;

import tastysearch.searchengine.services.reader.FileDataReader;

/**
 * Utility class to provide helper methods for {@link FileDataReader} implementations.
 */
public class ReaderUtil {

    private ReaderUtil() {
        // Prevent instantiation
    }

    /**
     * Convert a string to float using {@link Float#parseFloat(String)}
     * Supports fractional string a well.
     *
     * @param inputString string to parse as float
     * @return float equivalent of the inputString
     */
    public static float parseFloat(String inputString) {
        if (inputString.contains("/")) {
            String[] ratio = inputString.split("/");
            return Float.parseFloat(ratio[0]) / Float.parseFloat(ratio[1]);
        } else {
            return Float.parseFloat(inputString);
        }
    }
}
