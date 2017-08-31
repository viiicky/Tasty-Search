package home.searchengine.services.indexer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import home.searchengine.models.Review;
import home.searchengine.util.IndexerUtils;

/**
 * TODO: Share on SO and ask if there is a better way to do this
 *
 * TODO: Do this in parallel for different reviews in another implementation of Indexer,
 * also measure the performance difference of seq vs parallel implementation
 *
 * This class is not meant to be called explicitly.
 * Its being used at application startup.
 */
@Service
@Qualifier("sequentialIndexer")
public class SequentialIndexer implements Indexer {

	private Review[] reviews;

	private boolean indexed = false;

	private Map<String, Set<Integer>> indexes = new HashMap<>();

	@Override
	public void setReviews(Review[] reviews) {
		this.reviews = reviews;
	}

	@Override
	public void createIndexes() {
		for (int i = 0; i < this.reviews.length; i++) {
			List<String> tokens = IndexerUtils.getWordsFromString(this.reviews[i].getSummary());
			tokens.addAll(IndexerUtils.getWordsFromString((this.reviews[i].getText())));

			this.insertTokens(tokens, i);
		}
		this.indexed = true;
	}

	private void insertTokens(List<String> tokens, final int reviewId) {
		// when in DB, might need to use a separate column/field to maintain review id, here array index is sufficient
		tokens.forEach(t -> {
			t = t.trim().toLowerCase();
			if (this.indexes.get(t) == null) {
				Set<Integer> postingSet = new HashSet<>();
				postingSet.add(reviewId);
				this.indexes.put(t, postingSet);
			}
			else {
				this.indexes.get(t).add(reviewId);
			}
		});
	}

	@Override
	public Map<String, Set<Integer>> getIndexes() {
		if (!this.indexed) {
			throw new IllegalStateException("Cannot return indexes. Indexing is still in progress...");
		}
		return this.indexes;
	}

	@Override
	public Review[] getReviews() {
		return this.reviews;
	}

}
