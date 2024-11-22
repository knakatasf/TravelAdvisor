package hotelapp.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.model.entity.Review;
import hotelapp.model.parser.JsonDataParser;

import java.nio.file.Path;
import java.util.*;

/**
 * Model class stores Review objects and wordCounts in hashMaps.
 * Uses dependency injection for JsonParser so that it can parse Review json data into the hashMap.
 * This class is not thread safe.
 */
public class ReviewData implements DataModel<Review> {
    private JsonDataParser parser;
    private Map<String, SortedSet<Review>> reviewMap;
    private Map<String, SortedMap<Integer, SortedSet<Review>>> invertedIndex;

    public ReviewData(JsonDataParser parser) {
        this.parser = parser;
        reviewMap = new HashMap<>();
        invertedIndex = new HashMap<>();
    }

    /**
     * Loads json file using factory method, and add Review object to the hashMap and wordCount hashMap.
     * @param path to be loaded
     */
    @Override
    public void loadJson(Path path) {
        List<Review> reviews = parser.parse(path);
        for (Review review : reviews) {
            add(review);
            addToInvertedIndex(review);
        }
    }

    /**
     * Adds Review object to the hashMap.
     * @param review to be added to the hashMap.
     */
    @Override
    public void add(Review review) {
        String hotelId = review.getHotelId();
        reviewMap.computeIfAbsent(hotelId, e -> new TreeSet<>()).add(review);
    }

    /**
     * Adds wordCount to the hashMap; First, create wordCount hashMap and adds each word and reviews to the invertedIndex.
     * @param review to be added to the invertedIndex.
     */
    public void addToInvertedIndex(Review review) {
        String reviewText = review.getReviewText();
        Map<String, Integer> wordFrequency = new HashMap<>();
        String[] wordList = reviewText.split("[.,:; ]+");
        for (String word : wordList) {
            if (word.isEmpty())
                continue;
            word = word.toLowerCase();
            wordFrequency.compute(word, (k, v) -> v == null ? 1 : v + 1);
        }

        for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
            String word = entry.getKey();
            Integer frequency = entry.getValue();
            invertedIndex.computeIfAbsent(word, a -> new TreeMap<>(Collections.reverseOrder()))
                    .computeIfAbsent(frequency, b -> new TreeSet<>())
                    .add(review);
        }
    }

    /**
     * Finds Review object by hotelId. This is safe to user since it returns String.
     * @param hotelId to be searched.
     * @return String of the Review objects if exists.
     */
    @Override
    public String find(String hotelId) {
        if (reviewMap.containsKey(hotelId)) {
            StringBuilder sb = new StringBuilder();
            for (Review review : reviewMap.get(hotelId)) {
                sb.append("--------------------" + System.lineSeparator());
                sb.append(review.toString());
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * Finds Review object by hotelId. This is safe to user since it returns String.
     * @param hotelId to be searched.
     * @return reviewText of the Review object found by hotelId if exists.
     */
    public String findReviewText(String hotelId) {
        if (reviewMap.containsKey(hotelId)) {
            StringBuilder sb = new StringBuilder();
            for (Review review : reviewMap.get(hotelId)) {
                sb.append(review.getReviewText() + System.lineSeparator());
            }
            return sb.toString();
        }
        return "";
    }


    /**
     * Finds Review object by hotelId and return it in JsonObject.
     * @param hotelId to be found a Review object.
     * @param num indicates how many reviews to be returned
     * @return Optional of JsonObject of the Review objects found by the hotelId.
     */
    @Override
    public Optional<JsonObject> findInJson(String hotelId, int num) {
        if (reviewMap.containsKey(hotelId)) {
            JsonObject reviewsJo = new JsonObject();
            JsonArray reviewJa = new JsonArray();
            int count = 0;
            for (Review review : reviewMap.get(hotelId)) {
                reviewJa.add(review.serialize());
                if (++count >= num)
                    break;
            }
            reviewsJo.addProperty("hotelId", hotelId);
            reviewsJo.add("reviews", reviewJa);
            return Optional.of(reviewsJo);
        }
        return Optional.empty();
    }

    /**
     * Finds Review objects containing the keyword; Returns Review objects in String, so it is safe.
     * @param word to be searched.
     * @return Review objects in String.
     */
    public String findWord(String word) {
        if (invertedIndex.containsKey(word)) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Integer, SortedSet<Review>> reviewsEntry : invertedIndex.get(word).entrySet()) {
                int freq = reviewsEntry.getKey();
                for (Review review : reviewsEntry.getValue()) {
                    sb.append("--------------------" + System.lineSeparator());
                    sb.append(freq + System.lineSeparator());
                    sb.append(review);
                }
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * Finds Review object that contains a word passed.
     * @param word to be found a Review object.
     * @param num indicates how many reviews to be returned
     * @return Optional of JsonObject of the Review objects found by the word.
     */
    public Optional<JsonObject> findWordInJson(String word, int num) {
        if (invertedIndex.containsKey(word)) {
            JsonObject reviewsJo = new JsonObject();
            JsonArray reviewJa = new JsonArray();
            int count = 0;
            SortedMap<Integer, SortedSet<Review>> reviewsMap = invertedIndex.get(word);
            outerloop:
            for (SortedSet<Review> reviews : reviewsMap.values()) {
                for (Review review : reviews) {
                    reviewJa.add(review.serialize());
                    if (++count >= num)
                        break outerloop;
                }
            }
            reviewsJo.add("reviews", reviewJa);
            return Optional.of(reviewsJo);
        }
        return Optional.empty();
    }

}
