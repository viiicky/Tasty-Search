package home.searchengine.services.indexer;

import java.util.Map;
import java.util.Set;

import home.searchengine.models.Review;

public interface Indexer {
	/**
	 * to be used at application startup
	 * @param reviews
	 */
	void setReviews(Review[] reviews);

	/**
	 * to be used at application startup
	 */
	void createIndexes();

	Map<String, Set<Integer>> getIndexes();

	Review[] getReviews();
}
