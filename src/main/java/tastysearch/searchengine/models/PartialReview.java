package tastysearch.searchengine.models;

/**
 * Model class to be used while calculating rank and breaking ties for same rank using {@link #score}.
 * This class is used as an alternative to {@link Review} class while performing rank calculation.
 * <p>
 * Reason being, that if {@link Review} objects are very big, which contains a lot of data, moving the whole
 * object around while performing rank calculation on a huge amount of data might take a lot of time.
 * <p>
 * That is why this class is being created with just enough information to calculate the rank and break the ties.
 * Once rank calculation is done, {@link #reviewId} field of this class can get the actual {@link Review} object in O(1)
 * and thus the actual array/list will be populated in O(n) at the end.
 * <p>
 * Note: This might be premature optimization, specially considering the fact that at any time
 * only the references to the object will be moving around not the actual objects.
 * The actual benefit can be known only via performance testing.
 * <p>
 * Note: If in future using the actual {@link Review} class only, don't forget to move the overridden methods:
 * - {@link #equals(Object)}
 * - {@link #hashCode()}
 * - {@link #compareTo(PartialReview)}
 */
public class PartialReview implements Comparable<PartialReview> {

    private int reviewId;

    private float score;

    public PartialReview(int reviewId, float score) {
        this.reviewId = reviewId;
        this.score = score;
    }

    @Override
    public String toString() {
        return "PartialReview{" +
                "reviewId=" + reviewId +
                ", score=" + score +
                '}';
    }

    /*
    As the objects of this class are being used as key of a Map at a point in time, this allows the same objects(key)
    to be identified in the map based on object's reviewId.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartialReview rank = (PartialReview) o;

        return reviewId == rank.reviewId;
    }

    /*
    As the objects of this class are being used as key of a Map at a point in time, this allows the same objects(key)
    to be identified in the map based on object's reviewId.
     */
    @Override
    public int hashCode() {
        return reviewId;
    }

    /*
    As the objects of this class are being inserted in a PriorityQueue, this allows to maintain the order inside the
    queue based on object's score.
     */
    @Override
    public int compareTo(PartialReview o) {
        return -1 * Float.compare(this.score, o.score);
    }

    public int getReviewId() {
        return reviewId;
    }
}
