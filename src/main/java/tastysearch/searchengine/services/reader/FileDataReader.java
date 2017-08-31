package tastysearch.searchengine.services.reader;

import tastysearch.searchengine.models.Review;

import java.util.List;

/**
 * Defines contracts for reading files
 */
public interface FileDataReader {
    List<Review> deserializeData(String filePath);
}
