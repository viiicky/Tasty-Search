package home.searchengine.models;

public class ReviewRank implements Comparable<ReviewRank>{

	private int reviewId;

	private float score;

	public ReviewRank(int reviewId, float score) {
		this.reviewId = reviewId;
		this.score = score;
	}

	@Override
	public String toString() {
		return "ReviewRank{" +
				"reviewId=" + reviewId +
				", score=" + score +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ReviewRank rank = (ReviewRank) o;

		return reviewId == rank.reviewId;
	}

	@Override
	public int hashCode() {
		return reviewId;
	}

	@Override
	public int compareTo(ReviewRank o) {
		return -1 * Float.compare(this.score, o.score);
	}

	public int getReviewId() {
		return reviewId;
	}
}
