package hotelapp.contoller;


import com.google.gson.JsonObject;
import hotelapp.CONST;
import hotelapp.handler.ArgumentHandler;
import hotelapp.model.DataModel;
import hotelapp.model.HotelData;
import hotelapp.model.ThreadSafeReviewData;
import hotelapp.model.entity.Hotel;
import hotelapp.model.entity.Review;
import hotelapp.model.parser.HotelJsonParser;
import hotelapp.model.parser.ReviewJsonParser;
import hotelapp.traverser.JsonTraverser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * Suppose Server is a View, this class is a Controller.
 * Utilizes JsonTraverser to load data for DataModel.
 * Utilized ExecutorService and Phaser for multi-threading work.
 * Manipulates data in DataModel.
 */
public class HotelReviewController {
    private DataModel hotelData;
    private DataModel reviewData;
    private ExecutorService poolManager;
    private Phaser phaser;
    private static final Logger logger = LogManager.getLogger();

    public HotelReviewController() {
        phaser = new Phaser();
        hotelData = new HotelData(new HotelJsonParser());
        reviewData = new ThreadSafeReviewData(new ReviewJsonParser());
    }

    /**
     * Loads data to hotelData and reviewData.
     * @param args should contain 3 arguments -hotels, -reviews, and -threads.
     */
    public void loadData(String[] args) {
        ArgumentHandler handler = new ArgumentHandler();
        try {
            handler.parseArguments(args);
            int numOfThreads = Integer.parseInt(
                    handler.getArgumentOf(CONST.THREAD_FLAG).orElse(CONST.DEFAULT_THREADS));

            poolManager = Executors.newFixedThreadPool(numOfThreads);

            handler.getArgumentOf(CONST.HOTEL_FLAG).ifPresent(hotelPath ->
                    loadHotelData(Paths.get(hotelPath)));

            handler.getArgumentOf(CONST.REVIEW_FLAG).ifPresent(reviewPath ->
                    loadReviewData(Paths.get(reviewPath)));

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Loads Hotel data by invoking traverse static method. This is not multi-threading.
     * @param hotelJsonDirectory to be traversed and loaded data into hotelData.
     */
    private void loadHotelData(Path hotelJsonDirectory) {
        JsonTraverser.traverse(hotelJsonDirectory, hotelData);
    }

    /**
     * Loads Review data by invoking traverse static method. This is multi-threading logic.
     * @param reviewJsonDirectory to be traversed and loaded data into hotelData.
     */
    private void loadReviewData(Path reviewJsonDirectory) {
        JsonTraverser.concurrentTraverse(reviewJsonDirectory, reviewData, poolManager, phaser);
        await();
        shutdownPool();
    }

    /**
     * Makes the main thread wait until all the registered task to be completed.
     */
    private void await() {
        phaser.awaitAdvance(phaser.getPhase());
        logger.debug("All the workers finished its work.");
    }

    /**
     * Safely shutdowns the poolManager, by not accepting new task anymore and wait for 3 secs.
     */
    private void shutdownPool() {
        poolManager.shutdown();
        try {
            poolManager.awaitTermination(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        logger.debug("Shut down the pool.");
    }

    /**
     * Finds Hotel object by hotelId in hotelData.
     * @param hotelId to be searched.
     * @return Hotel object in String.
     */
    public String findHotel(String hotelId) {
        return hotelData.find(hotelId);
    }

    /**
     * Find hotelName by hotelId in hotelData.
     * @param hotelId to be searched.
     * @return hotelName if found.
     */
    public String findHotelName(String hotelId) { return ((HotelData)hotelData).findHotelName(hotelId); }

    /**
     * Finds Hotel objects by hotelId in JsonObject.
     * @param hotelId to be searched.
     * @param num should be ignored.
     * @return Optional of JsonObject of Hotel object if found.
     */
    public Optional<JsonObject> findHotelInJson(String hotelId, int num) {
        return hotelData.findInJson(hotelId, num);
    }

    /**
     * Finds Review objects by hotelId in reviewData.
     * @param hotelId to be searched.
     * @return Review objects in String.
     */
    public String findReviews(String hotelId) {
        return reviewData.find(hotelId);
    }

    /**
     * Finds reviewText by hotelId.
     * @param hotelId to be searched.
     * @return reviewText in String if found.
     */
    public String findReviewText(String hotelId) { return ((ThreadSafeReviewData)reviewData).findReviewText(hotelId); }

    /**
     * Finds Review object by hotelId in JsonObject.
     * @param hotelId to be searched.
     * @param num indicated how many Review objects to be returned.
     * @return Optional of JsonObject of Review object if found.
     */
    public Optional<JsonObject> findReviewInJson(String hotelId, int num) {
        return reviewData.findInJson(hotelId, num);
    }

    /**
     * Finds Review objects containing the keyword passed.
     * @param word to be searched.
     * @return Review objects in String.
     */
    public String findWord(String word) {
        return ((ThreadSafeReviewData)reviewData).findWord(word);
    }

    /**
     * Finds Review object by word in JsonObject.
     * @param word to be searched.
     * @param num indicated how many Review objects to be returned.
     * @return Optional of JsonObject of Review object if found.
     */
    public Optional<JsonObject> findWordInJson(String word, int num) {
        return ((ThreadSafeReviewData)reviewData).findWordInJson(word, num);
    }

    /**
     * Returns an Optional of a cloned Hotel object by hotelId.
     * @param hotelId to be searched
     * @return Optional of the cloned Hotel object.
     */
    public Optional<Hotel> findHotelByValue(String hotelId) {
        return ((HotelData)hotelData).findHotelByValue(hotelId);
    }

    /**
     * Return a list of hotels contains the keyword in its names by value.
     * @param keyword to be searched.
     * @return List of the cloned hotel objects.
     */
    public List<Hotel> findHotelsByKeyword(String keyword) {
        return ((HotelData)hotelData).findHotelsByKeyword(keyword);
    }

    /**
     * Returns a list of the cloned reviews of the hotel by hotelId.
     * @param hotelId to be searched.
     * @return a list of the cloned review objects of the hotel.
     */
    public List<Review> findReviewsByValue(String hotelId) {
        return ((ThreadSafeReviewData)reviewData).findReviewsByValue(hotelId);
    }

    /**
     * Adds a review to the map.
     * @param review to be added.
     */
    public void addReview(Review review) {
        reviewData.add(review);
    }

    /**
     * Returns a list of all the hotels in the map, which are cloned.
     * @return a list of all the cloned hotel object.
     */
    public List<Hotel> loadAllHotelsByValue() {
        return ((HotelData)hotelData).loadAllHotelsByValue();
    }

    /**
     * Return an Optional of a cloned review object by hotelId and reviewId.
     * @param hotelId to be searched.
     * @param reviewId to be searched.
     * @return Optional of the cloned review object.
     */
    public Optional<Review> findReviewByValue(String hotelId, String reviewId) {
        return ((ThreadSafeReviewData)reviewData).findReviewByValue(hotelId, reviewId);
    }

    /**
     * Removes a review object from the map.
     * @param hotelId to be searched.
     * @param reviewId to be searched.
     */
    public void removeReview(String hotelId, String reviewId) {
        ((ThreadSafeReviewData)reviewData).removeReview(hotelId, reviewId);
    }

}
