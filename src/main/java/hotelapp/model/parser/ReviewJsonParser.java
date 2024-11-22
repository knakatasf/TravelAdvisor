package hotelapp.model.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hotelapp.model.entity.Review;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * JsonParser to parse Review object from a json file; Implements factory method.
 * This class can be injected into model class so that model class can store Review objects.
 */
public class ReviewJsonParser implements JsonDataParser<Review> {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parses Review objects from a json file.
     * @param p to be parsed Review objects.
     * @return ArrayList<Review>
     */
    @Override
    public List<Review> parse(Path p) {
        List<Review> reviews = new ArrayList<>();
        try {
            FileReader fr = new FileReader(p.toString());
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(fr);
            JsonArray jsonArray = jo.getAsJsonObject("reviewDetails")
                    .getAsJsonObject("reviewCollection")
                    .getAsJsonArray("review");

            for (JsonElement element : jsonArray) {
                JsonObject jsonObj = element.getAsJsonObject();

                String hotelId = jsonObj.get("hotelId").getAsString();
                String reviewId = jsonObj.get("reviewId").getAsString();
                String title = jsonObj.get("title").getAsString();
                String reviewText = jsonObj.get("reviewText").getAsString();
                String userNickname = jsonObj.get("userNickname").getAsString();
                String submissionDate = jsonObj.get("reviewSubmissionDate").getAsString();

                Review.ReviewBuilder builder = new Review.ReviewBuilder();
                reviews.add(builder.hotelId(hotelId).reviewId(reviewId)
                        .title(title).reviewText(reviewText)
                        .userNickname(userNickname).submissionDate(submissionDate)
                        .build());
            }
        } catch (FileNotFoundException e) {
            logger.error("File couldn't be found: " + p);
            System.out.println("File couldn't be found: " + e);
        }
        return reviews;
    }
}
