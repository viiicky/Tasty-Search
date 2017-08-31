package tastysearch.searchengine.services.indexer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tastysearch.searchengine.models.Review;
import tastysearch.searchengine.util.IndexerUtils;

import java.util.*;

/**
 * Class responsible to create the inverted indexMap for the {@link Review} documents in a sequential manner.
 * This class is not meant to be called explicitly. Its being used at application startup.
 * <p>
 * Note: Even this might be made as type agnostic, needs to think more on it.
 * <p>
 * TODO: Currently the requirement is to store the indexes in memory, in future DB can come into picture, at that
 * point of time we should interface the storing and accessing index code out of here to manage it better.
 * <p>
 * TODO: Add a parallel indexer implementation of {@link Indexer} in future and measure the performance of both.
 */
@Service
@Qualifier("sequentialIndexer")
public class SequentialIndexer implements Indexer {

    private Review[] reviews;

    private boolean indexed = false;

    private Map<String, Set<Integer>> indexMap = new HashMap<>();

    /**
     * Iterates {@link #reviews} array and creates tokens from string of each {@link Review}'s {@link Review#summary}
     * and {@link Review#text}
     * <p>
     * Calls {@link #insertTokens(List, int)} to insert those tokens as index(key) in a map and the corresponding
     * review id in the posting-set present as the values of those index in the map.
     * <p>
     * Sets {@link #indexed} to true once indexing for all the reviews is done.
     */
    @Override
    public void createIndexes() {
        for (int i = 0; i < this.reviews.length; i++) {
            List<String> tokens = IndexerUtils.getWordsFromString(this.reviews[i].getSummary());
            tokens.addAll(IndexerUtils.getWordsFromString((this.reviews[i].getText())));

            this.insertTokens(tokens, i);
        }
        this.indexed = true;
    }

    /**
     * Inserts each token as a key in the {@link #indexMap} and its value will be a posting-set that contains
     * review ids.
     * Since this implementation is in-memory, the array index serves as review id.
     *
     * @param tokens   list of tokens to be indexed
     * @param reviewId id of the review that will be added across the token's posting-set
     */
    private void insertTokens(List<String> tokens, final int reviewId) {
        // when in DB, might need to use a separate column/field to maintain review id, here array index is sufficient
        tokens.forEach(t -> this.indexMap.computeIfAbsent(t.trim(), k -> new HashSet<>()).add(reviewId));
    }

    @Override
    public Map<String, Set<Integer>> getIndexMap() {
        if (!this.indexed) {
            throw new IllegalStateException("Indexing is still in progress. Consider retrying after sometime.");
        }
        return this.indexMap;
    }

    @Override
    public Review[] getReviews() {
        return this.reviews;
    }

    @Override
    public void setReviews(Review[] reviews) {
        this.reviews = reviews;
    }

}
