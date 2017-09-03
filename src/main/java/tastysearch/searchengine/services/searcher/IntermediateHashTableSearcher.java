package tastysearch.searchengine.services.searcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tastysearch.searchengine.models.PartialReview;
import tastysearch.searchengine.models.Review;
import tastysearch.searchengine.services.indexer.Indexer;

import java.util.*;

/**
 * Search service that provides searching functionality using intermediate map approach.
 * <p>
 * TODO: Try another implementation of SearchService with the bulk sorting approach and measure the performance.
 * TODO: Try yet another implementation which is quite same to this implementation, only change is the final sorting
 * of reviews which is being done while insertion now, let it happen at end. Check the performance.
 * This approach is anyway better than the above two discussed approach but still try and see the numbers.
 */
@Service
@Qualifier("intermediateHashTableSearcher")
public class IntermediateHashTableSearcher implements SearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntermediateHashTableSearcher.class);
    private final int numberOfReviewsToReturn;
    private final Indexer indexer;

    @Autowired
    public IntermediateHashTableSearcher(@Value("${number-of-reviews-to-return}") final int numberOfReviewsToReturn,
                                         @Qualifier("sequentialIndexer") final Indexer indexer) {

        this.numberOfReviewsToReturn = numberOfReviewsToReturn;
        this.indexer = indexer;
    }

    /**
     * Search required reviews using the below methods:
     * - {@link #createReviewRankHashTable(Collection, Map, Review[])}
     * - {@link #invertHashTable(Map)}
     * - {@link #getTopKResults(Map, int, int, Review[])}
     *
     * @param searchTokens token strings to search the reviews
     * @return the searched reviews
     */
    @Override
    public List<Review> searchReviews(Collection<String> searchTokens) {
        Map<String, Set<Integer>> indexMap = this.indexer.getIndexMap();
        Review[] indexedReviews = this.indexer.getReviews();

        // prepare review-rank table
        Map<PartialReview, Integer> reviewRankTable = this.createReviewRankHashTable(searchTokens, indexMap, indexedReviews);

        // Uncomment while debugging. Not relying on log levels, commenting instead as it is inside a big loop
        /*LOGGER.debug("Review Rank Table:");
        reviewRankTable.forEach((k, v) -> LOGGER.debug("Review: {}, Rank: {}", k, v));*/

        // create intermediate hash table (just invert the above review-rank table to rank-review table)
        Map<Integer, Queue<PartialReview>> rankReviewTable = this.invertHashTable(reviewRankTable);

        // Uncomment while debugging. Not relying on log levels, commenting instead as it is inside a big loop
        /*LOGGER.debug("Rank Review Table:");
        rankReviewTable.forEach((k, v) -> LOGGER.debug("Rank: {}, Review: {}", k, v));*/

        // prepare the returning array (return top K result from rankReviewTable)
        return this.getTopKResults(rankReviewTable, this.numberOfReviewsToReturn, searchTokens.size(), indexedReviews);
    }

    /**
     * Creates a map with reviews as key and their rank as value.
     * <p>
     * For each token in the input token list, it fetches the posting set of reviews from the indexMap.
     * All the reviews fetched in each such iteration gets their rank incremented by 1 each time, starting with 0.
     * <p>
     * For example:
     * If the search tokens are: {token1=good, token2=dog, token3=food} and there exists say thirteen reviews in the
     * system which matches at least one of the search token. Then a possible review-rank table that would be created
     * would be:
     * <p>
     * Review: PartialReview{reviewId=16, score=5.0}, Rank: 3
     * Review: PartialReview{reviewId=17, score=5.0}, Rank: 1
     * Review: PartialReview{reviewId=3, score=5.0}, Rank: 1
     * Review: PartialReview{reviewId=4, score=5.0}, Rank: 1
     * Review: PartialReview{reviewId=5, score=4.0}, Rank: 1
     * Review: PartialReview{reviewId=7, score=2.0}, Rank: 1
     * Review: PartialReview{reviewId=9, score=5.0}, Rank: 3
     * Review: PartialReview{reviewId=11, score=1.0}, Rank: 1
     * Review: PartialReview{reviewId=13, score=2.0}, Rank: 1
     * <p>
     * The above signifies that reviews with review id 16 and 9 has 3 search terms matching (thus a rank of 3),
     * while all the other reviews have just 1 search term matching (thus a rank of 1)
     * <p>
     * The higher the rank the more relevant the result is. Also, its worth noting that for the above example any
     * review can have the rank of either 1 or 2 or 3 only. No review will exceed the rank more than the number of
     * search tokens provided. No review with 0 rank will be entertained in this table.
     *
     * @param tokens        list of search tokens that will be used to fetch posting-set of reviews
     * @param indexMap      the map which contains the (tokens, posting-set) pair
     * @param actualReviews {@link Review}[]
     * @return map created with reviews as key and their ranks as value
     * @see Review
     * @see Indexer#createIndexes()
     */
    private Map<PartialReview, Integer> createReviewRankHashTable(Collection<String> tokens,
                                                                  Map<String, Set<Integer>> indexMap,
                                                                  Review[] actualReviews) {

        Map<PartialReview, Integer> reviewRankTable = new HashMap<>();
        for (String token : tokens) {
            Set<Integer> postingSet = indexMap.get(token.trim().toLowerCase());
            Optional.ofNullable(postingSet).ifPresent(s -> s.forEach(reviewId -> {  // if the input token doesn't exists in our system, do nothing
                PartialReview rank = new PartialReview(reviewId, actualReviews[reviewId].getScore());
                reviewRankTable.merge(rank, 1, (a, b) -> a + b);    // in a given posting set no same reviews exists as its a set
            }));
        }
        return reviewRankTable;
    }

    /**
     * Creates a map by inverting the provided map.
     * <p>
     * Considers all the values present in the input map as the key of output map
     * and places the key corresponding to those values in the input map as value of the respective key in output map.
     * As the values can repeat in input table, all the keys having same value are actually converged in a heap kind
     * of data structure, more specifically PriorityQueue to be placed as values in the output table.
     * <p>
     * It means that the key of the input table must implement the Comparable interface so that a particular order can
     * be maintained while inserting values in the heap.
     * <p>
     * Example:
     * Let us continue with the above example of {@link #createReviewRankHashTable(Collection, Map, Review[])},
     * where we created a review-rank table as:
     * <p>
     * Review: PartialReview{reviewId=16, score=5.0}, Rank: 3
     * Review: PartialReview{reviewId=17, score=5.0}, Rank: 1
     * Review: PartialReview{reviewId=3, score=5.0}, Rank: 1
     * Review: PartialReview{reviewId=4, score=5.0}, Rank: 1
     * Review: PartialReview{reviewId=5, score=4.0}, Rank: 1
     * Review: PartialReview{reviewId=7, score=2.0}, Rank: 1
     * Review: PartialReview{reviewId=9, score=5.0}, Rank: 3
     * Review: PartialReview{reviewId=11, score=1.0}, Rank: 1
     * Review: PartialReview{reviewId=13, score=2.0}, Rank: 1
     * <p>
     * Assumed that PartialReview implements Comparable in such a way that
     * a review with higher score is given more priority as compared to a review with lower score,
     * and the reviews with equal scores are given arbitrary priority,
     * while insertion in the priority queue.
     * <p>
     * In such a case, this method will invert the input review-rank table breaking the tie amongst PartialReviews
     * for same score, to rank-review table as follows:
     * <p>
     * Rank: 1, Review: [
     * PartialReview{reviewId=17, score=5.0},
     * PartialReview{reviewId=3, score=5.0},
     * PartialReview{reviewId=4, score=5.0},
     * PartialReview{reviewId=5, score=4.0},
     * PartialReview{reviewId=7, score=2.0},
     * PartialReview{reviewId=11, score=1.0},
     * PartialReview{reviewId=13, score=2.0}
     * ]
     * <p>
     * Rank: 3, Review: [
     * PartialReview{reviewId=16, score=5.0},
     * PartialReview{reviewId=9, score=5.0}
     * ]
     * <p>
     * Also note we will get the items from the priority queue in the expected order breaking the tie when we
     * actually {@link PriorityQueue#poll()} the elements from the queue. The above order in the example is just a
     * random order.
     *
     * @param inputTable input map to be inverted
     * @return the multi-value map which is an inversion of the input map
     * @see PriorityQueue
     * @see Comparable
     * @see PartialReview
     * @see PartialReview#score
     * <p>
     * Note: Think if this can be made type agnostic.
     * TODO: Parallelize the inversion and measure performance
     */
    private Map<Integer, Queue<PartialReview>> invertHashTable(Map<PartialReview, Integer> inputTable) {
        Map<Integer, Queue<PartialReview>> outputTable = new HashMap<>();
        inputTable.forEach((k, v) -> outputTable.computeIfAbsent(v, PriorityQueue::new).add(k));
        return outputTable;
    }

    /**
     * Returns top k reviews from actual reviews array based on {@link PartialReview#reviewId} present inside input map.
     * The selection starts with the key with highest value in the map, collecting all the values inside the heap
     * corresponding to that key in the map, and then moving down to lower key values.
     * <p>
     * At any point of time when the number of collected reviews reach k, the execution is stopped
     * and the reviews array is returned.
     * <p>
     * If there doesn't exist k reviews in the map, null is returned for remaining reviews inside the array.
     * <p>
     * Element are extracted from heap using {@link PriorityQueue#poll()}, thus getting the reviews as per priority
     * from a given heap
     *
     * @param map           which contains scores and review ids
     * @param k             number of reviews to be returned
     * @param topKey        the highest score in the map
     * @param actualReviews the array containing all the {@link Review}s
     * @return selected reviews
     */
    private List<Review> getTopKResults(Map<Integer, Queue<PartialReview>> map, int k, int topKey, Review[] actualReviews) {
        List<Review> topKReviews = new ArrayList<>();
        for (int i = topKey; i > 0; i--) {
            Queue<PartialReview> partialReviewPriorityQueue = map.get(i);

            // poll from priority queue to retrieve in order
            while (partialReviewPriorityQueue != null && !partialReviewPriorityQueue.isEmpty()) {
                topKReviews.add(actualReviews[partialReviewPriorityQueue.poll().getReviewId()]);
                if (topKReviews.size() == k) {
                    return topKReviews;
                }
            }
        }

        return topKReviews;
    }
}
