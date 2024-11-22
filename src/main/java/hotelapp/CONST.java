package hotelapp;

import java.util.Set;

public class CONST {
    public static final String HOTEL_FLAG = "-hotels";
    public static final String REVIEW_FLAG = "-reviews";
    public static final String THREAD_FLAG = "-threads";
    public static final String DEFAULT_THREADS = "3";
    public static final Set<String> FLAG_SET = Set.of(HOTEL_FLAG, REVIEW_FLAG, THREAD_FLAG);

    public static final String FIND_HOTEL_QUERY = "findHotel";
    public static final String FIND_REVIEWS_QUERY = "findReviews";
    public static final String FIND_WORD_QUERY = "findWord";
    public static final Set<String> QUERY_SET = Set.of(FIND_HOTEL_QUERY, FIND_REVIEWS_QUERY, FIND_WORD_QUERY);
}
