package home.searchengine.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import home.searchengine.models.Review;
import home.searchengine.util.IndexerUtils;

/**
 * TODO: Share on SO and ask if there is a better way to do this
 */
public class Indexer {

	public Map<String, Set<Integer>> createIndexes(final Review[] reviews) {
		Map<String, Set<Integer>> indexes = new HashMap<>();

		for (int i = 0; i < reviews.length; i++) {    // TODO: do this in parallel for different reviews, also
			// measure the performance difference of seq vs parallel
			List<String> tokens = IndexerUtils.getWordsFromString(reviews[i].getSummary());
			tokens.addAll(IndexerUtils.getWordsFromString((reviews[i].getText())));

			this.insertTokens(indexes, tokens, i);
		}

		return indexes;
	}

	private void insertTokens(Map<String, Set<Integer>> indexMap, List<String> tokens, final int reviewId) {
		// when in DB, might need to use a separate column/field to maintain review id, here array index is sufficient
		tokens.forEach(t -> {
			t = t.trim().toLowerCase();
			if (indexMap.get(t) == null) {
				Set<Integer> postingSet = new HashSet<>();
				postingSet.add(reviewId);
				indexMap.put(t, postingSet);
			}
			else {
				indexMap.get(t).add(reviewId);
			}
		});
	}

}
