package tastysearch.searchengine.util;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to provide helper methods for {@link tastysearch.searchengine.services.indexer.Indexer} implementations
 */
public class IndexerUtils {

    private IndexerUtils() {
        // Prevent instantiation
    }

    /**
     * Takes the input string and break them into words as per default locale.
     * All the words are converted to a lower case string.
     *
     * @param inputString string to be broken
     * @return List of words
     */
    public static List<String> getWordsFromString(String inputString) {
        List<String> tokens = new ArrayList<>();

        BreakIterator iterator = BreakIterator.getWordInstance();
        iterator.setText(inputString);

        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            tokens.add(inputString.substring(start, end).toLowerCase());
        }

        return tokens;
    }
}
