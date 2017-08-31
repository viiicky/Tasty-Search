package tastysearch.searchengine.models;

/**
 * POJO class to model a review document.
 */
public class Review {

    private String productId;

    private String userId;

    private String profileName;

    private String helpfulness;    // make it float using ReaderUtil in future if any operation is being performed on this

    private float score;

    private long time;    // make it instant in future if any date operation is being performed on this

    private String summary;

    private String text;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getHelpfulness() {
        return helpfulness;
    }

    public void setHelpfulness(String helpfulness) {
        this.helpfulness = helpfulness;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

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
