package home.searchengine.models;

// TODO: Add javadoc
public class Review {

	private String productId;

	private String userId;

	private String profileName;

	private String helpfulness;	// make it float using ReaderUtil in future if any operation is being performed on this

	private float score;

	private long time;	// make it instant in future if any date operation is being performed on this

	private String summary;

	private String text;

	public String getProductId() {
		return productId;
	}

	public String getUserId() {
		return userId;
	}

	public String getProfileName() {
		return profileName;
	}

	public String getHelpfulness() {
		return helpfulness;
	}

	public float getScore() {
		return score;
	}

	public long getTime() {
		return time;
	}

	public String getSummary() {
		return summary;
	}

	public String getText() {
		return text;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public void setHelpfulness(String helpfulness) {
		this.helpfulness = helpfulness;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setText(String text) {
		this.text = text;
	}

	// TODO: Remove this method once development is done
	@Override
	public String toString() {
		return "Review{" +
				"productId='" + productId + '\'' +
				", userId='" + userId + '\'' +
				", profileName='" + profileName + '\'' +
				", helpfulness=" + helpfulness +
				", score=" + score +
				", time=" + time +
				", summary='" + summary + '\'' +
				", text='" + text + '\'' +
				'}';
	}
}
