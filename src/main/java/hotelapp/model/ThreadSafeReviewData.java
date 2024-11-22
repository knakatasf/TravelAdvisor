package hotelapp.model;

import com.google.gson.JsonObject;
import hotelapp.model.entity.Review;
import hotelapp.model.parser.JsonDataParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Extends ReviewData so that the data member in ReviewData is thread safe.
 * Utilizes two ReentrantReadWriteLocks to protect reviewMap and invertedMap.
 */
public class ThreadSafeReviewData extends ReviewData {
    private ReentrantReadWriteLock lockForReviewMap;
    private ReentrantReadWriteLock lockForInvertedIndex;
    private static final Logger logger = LogManager.getLogger();

    public ThreadSafeReviewData(JsonDataParser parser) {
        super(parser);
        lockForReviewMap = new ReentrantReadWriteLock();
        lockForInvertedIndex = new ReentrantReadWriteLock();
    }

    /**
     * Simply calls super class's loadJson.
     * @param path to be loaded
     */
    @Override
    public void loadJson(Path path) {
        logger.debug("Started loading a json file: " + path);
        super.loadJson(path);
    }

    /**
     * Before invoking super class's add method, protects reviewMap by writeLock.
     * @param review to be added.
     */
    @Override
    public void add(Review review) {
        logger.debug("Adding a review to reviewMap: " + review.getTitle());
        try {
            lockForReviewMap.writeLock().lock();
            super.add(review);
        } finally {
            lockForReviewMap.writeLock().unlock();
        }
    }

    /**
     * Before invoking super class's addToInvertedIndex method, protects invertedIndex by writeLock.
     * @param review to be added.
     */
    @Override
    public void addToInvertedIndex(Review review) {
        logger.debug("Adding a review to invertedIndex: " + review.getTitle());
        try {
            lockForInvertedIndex.writeLock().lock();
            super.addToInvertedIndex(review);
        } finally {
            lockForInvertedIndex.writeLock().unlock();
        }
    }

    /**
     * Before invoking super class's find method, protects reviewMap by readLock.
     * @param hotelId to be searched.
     * @return Hotel object in String
     */
    @Override
    public String find(String hotelId) {
        logger.debug("Finding a review for : " + hotelId);
        try {
            lockForReviewMap.readLock().lock();
            return super.find(hotelId);
        } finally {
            lockForReviewMap.readLock().unlock();
        }
    }

    /**
     * Before invoking super class's findInJson method, protects invertedIndex by readLock.
     * @param hotelId to be searched.
     * @param num indicates how many reviews to be returned.
     * @return Review objects in JsonObject
     */
    @Override
    public Optional<JsonObject> findInJson(String hotelId, int num) {
        logger.debug("Finding a review for : " + hotelId);
        try {
            lockForReviewMap.readLock().lock();
            return super.findInJson(hotelId, num);
        } finally {
            lockForReviewMap.readLock().unlock();
        }
    }

    /**
     * Before invoking super class's findWord method, protects invertedIndex by readLock.
     * @param word to be searched.
     * @return Review objects in String
     */
    @Override
    public String findWord(String word) {
        logger.debug("Finding a word in reviews: " + word);
        try {
            lockForInvertedIndex.readLock().lock();
            return super.findWord(word);
        } finally {
            lockForInvertedIndex.readLock().unlock();
        }
    }

    /**
     * Before invoking super class's findWordInJson method, protects invertedIndex by readLock.
     * @param word to be searched.
     * @param num indicates how many reviews to be returned.
     * @return Review objects in JsonObject
     */
    @Override
    public Optional<JsonObject> findWordInJson(String word, int num) {
        logger.debug("Finding a word in reviews: " + word);
        try {
            lockForInvertedIndex.readLock().lock();
            return super.findWordInJson(word, num);
        } finally {
            lockForInvertedIndex.readLock().unlock();
        }
    }
}

