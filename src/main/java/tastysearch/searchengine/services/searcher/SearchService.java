package tastysearch.searchengine.services.searcher;

import tastysearch.searchengine.models.Review;

import java.util.Collection;
import java.util.List;

/**
 * Defines contracts for searching reviews.
 * Note: analyze if this can be type agnostic.
 */
public interface SearchService {
    List<Review> searchReviews(Collection<String> inputTokens);
}
