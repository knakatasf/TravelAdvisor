package hotelapp.model.entity;

import com.google.gson.JsonObject;

/**
 * Entity class for a Hotel object.
 * Utilizes builder pattern to ensure all the data member is valid.
 */
public class Review implements Comparable<Review> {
    private String hotelId;
    private String reviewId;
    private String title;
    private String reviewText;
    private String userNickname;
    private String submissionDate;

    public Review(ReviewBuilder builder) {
        this.hotelId = builder.hotelId;
        this.reviewId = builder.reviewId;
        this.title = builder.title;
        this.reviewText = builder.reviewText;
        this.userNickname = builder.userNickname;
        this.submissionDate = builder.submissionDate;
    }

    public static class ReviewBuilder {
        private String hotelId;
        private String reviewId;
        private String title;
        private String reviewText;
        private String userNickname;
        private String submissionDate;

        public ReviewBuilder hotelId(String hotelId) {
            this.hotelId = hotelId;
            return this;
        }

        public ReviewBuilder reviewId(String reviewId) {
            this.reviewId = reviewId;
            return this;
        }

        public ReviewBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ReviewBuilder reviewText(String reviewText) {
            this.reviewText = reviewText;
            return this;
        }

        public ReviewBuilder userNickname(String userNickname) {
            this.userNickname = userNickname;
            return this;
        }

        public ReviewBuilder submissionDate(String submissionDate) {
            this.submissionDate = submissionDate;
            return this;
        }

        /**
         * Builds a Review object using builder patter; Ensures all the data members are valid.
         * @return Review object
         */
        public Review build() {
            validate();
            return new Review(this);
        }

        private void validate() {
            if (userNickname.isEmpty() || userNickname.equals("")) {
                userNickname = "Anonymous";
            }
        }
    }

    public String getHotelId() { return hotelId; }
    public String getReviewId() { return reviewId; }
    public String getTitle() { return title; }
    public String getReviewText() { return reviewText; }
    public String getUserNickname() { return userNickname; }
    public String getSubmissionDate() { return submissionDate; }

    /**
     * Serializes a Review object to a Json Object.
     * @return jsonObject of the review object.
     */
    public JsonObject serialize() {
        JsonObject reviewJo = new JsonObject();
        reviewJo.addProperty("reviewId", getReviewId());
        reviewJo.addProperty("title", getTitle());
        reviewJo.addProperty("user", getUserNickname());
        reviewJo.addProperty("reviewText", getReviewText());
        reviewJo.addProperty("date", getSubmissionDate());
        return reviewJo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Review by " + getUserNickname() + " on " + getSubmissionDate() + System.lineSeparator());
        sb.append("ReviewId: " + getReviewId() + System.lineSeparator());
        sb.append(getTitle() + System.lineSeparator());
        sb.append(getReviewText() + System.lineSeparator());
        return sb.toString();
    }

    @Override
    public int compareTo(Review other) {
        int comp = this.getSubmissionDate().compareTo(other.getSubmissionDate());
        if (comp == 0)
            return this.getReviewId().compareTo(other.getReviewId());
        return -comp;
    }
}