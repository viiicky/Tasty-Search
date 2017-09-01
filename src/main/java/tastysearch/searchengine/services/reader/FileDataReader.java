package tastysearch.searchengine.services.reader;

import tastysearch.searchengine.models.Review;

import java.io.IOException;
import java.util.List;

/**
 * Defines contracts for reading files
 */
public interface FileDataReader {
    List<Review> deserializeData(String filePath) throws IOException;
}
