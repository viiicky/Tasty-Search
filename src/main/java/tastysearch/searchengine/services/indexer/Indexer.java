package tastysearch.searchengine.services.indexer;

import tastysearch.searchengine.models.Review;

import java.util.Map;
import java.util.Set;

/**
 * Defines contracts for creating indexes.
 * <p>
 * Note: This interface can be made type agnostic.
 * <p>
 * TODO: Currently the requirement is to store the indexes in memory, in future DB can come into picture, at that
 * point of time we should interface the storing index code and accessing index code out of here to manage it better.
 */
public interface Indexer {

    /*
    to be used at application startup
     */
    void createIndexes();

    Map<String, Set<Integer>> getIndexMap();

    Review[] getReviews();

    /*
    to be used at application startup
     */
    void setReviews(Review[] reviews);
}
