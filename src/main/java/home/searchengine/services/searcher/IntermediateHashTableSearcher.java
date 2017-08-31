package home.searchengine.services.searcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import home.searchengine.models.Review;
import home.searchengine.models.ReviewRank;
import home.searchengine.services.indexer.Indexer;

/**
 * TODO: Try sorting approach in another implementation of SearchService
 * // adn measure the performance of both
 */
@Service
@Qualifier("intermediateHashTableSearcher")
public class IntermediateHashTableSearcher implements SearchService {

	private final int numberOfReviewsToReturn;

	private final Indexer indexer;

	@Autowired
	public IntermediateHashTableSearcher(@Value("${number-of-reviews-to-return}") final int numberOfReviewsToReturn,
			@Qualifier("sequentialIndexer") final Indexer indexer) {

		this.numberOfReviewsToReturn = numberOfReviewsToReturn;
		this.indexer = indexer;
	}

	@Override
	public Review[] searchReviews(Collection<String> inputTokens) {
		Map<String, Set<Integer>> indexes = this.indexer.getIndexes();
		Review[] indexedReviews = this.indexer.getReviews();

		// prepare score table
		Map<ReviewRank, Integer> reviewScoreTable = new HashMap<>();
		for (String token : inputTokens) {
			String trimmedToken = token.trim().toLowerCase();

			Set<Integer> postingSet = indexes.get(trimmedToken);
			if (postingSet == null) {
				continue;
			}
			postingSet.forEach(reviewId -> {
				ReviewRank rank = new ReviewRank(reviewId, indexedReviews[reviewId].getScore());

				if (reviewScoreTable.get(rank) == null) {
					reviewScoreTable.put(rank, 1);
				}else {
					reviewScoreTable.put(rank, reviewScoreTable.get(rank) + 1);
				}
			});
		}

		System.out.println("SCORE TABLE BELOW:");
		reviewScoreTable.forEach((k, v) -> {
			System.out.println("partial review: " + k + " score: " + v + " actual review: " + indexedReviews[k
					.getReviewId()]);
		});
		/////////////////////////////////////////////////////////////////////////////////////

		// create intermediate hash table (just invert the above score-table)
		Map<Integer, Queue<ReviewRank>> scoreReviewMap = new HashMap<>();
		for (Map.Entry<ReviewRank, Integer> entry : reviewScoreTable.entrySet()) {
			int score = entry.getValue();
			ReviewRank reviewRank = entry.getKey();

			Queue<ReviewRank> reviewRankPriorityQueue = scoreReviewMap.get(score);
			if (reviewRankPriorityQueue == null) {
				reviewRankPriorityQueue = new PriorityQueue<>();
				reviewRankPriorityQueue.add(reviewRank);
				scoreReviewMap.put(score, reviewRankPriorityQueue);
			}
			else {
				reviewRankPriorityQueue.add(reviewRank);
			}
		}

		System.out.println("SCORE REVIEW MAP");
		scoreReviewMap.forEach((k, v) -> {
			System.out.println("score: " + k + " partial reviews: " + v);
		});

		///////////////////////////////////////////////////////////////////////////////////////////////

		// prepare the returning array (return top K result from scoreReviewMap)
		Review[] searchedReviews = new Review[numberOfReviewsToReturn];
		int j = 0;
		for (int i = inputTokens.size(); i > 0; i--) {
			Queue<ReviewRank> reviewRankPriorityQueue = scoreReviewMap.get(i);
			if (reviewRankPriorityQueue == null) {
				continue;
			}

			/*for (ReviewRank reviewRank : reviewRankPriorityQueue) {
				searchedReviews[j] = indexedReviews[reviewRank.getReviewId()];
				j += 1;
				if (j == numberOfReviewsToReturn) {
					break;
				}
			}*/
			// poll from priority queue to retrieve in order
			while(! reviewRankPriorityQueue.isEmpty()){
				searchedReviews[j] = indexedReviews[reviewRankPriorityQueue.poll().getReviewId()];
				j += 1;
				if (j == numberOfReviewsToReturn){
					break;
				}
			}
		}

		return searchedReviews;

	}
}
