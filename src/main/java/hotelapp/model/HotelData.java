package hotelapp.model;

import com.google.gson.JsonObject;
import hotelapp.model.entity.Hotel;
import hotelapp.model.parser.JsonDataParser;

import java.nio.file.Path;
import java.util.*;

/**
 * Model class stores Hotel object in a hashMap.
 * Uses dependency injection for JsonParser so that it can parse Hotel json data into the hashMap.
 * This class is not thread safe.
 */
public class HotelData implements DataModel<Hotel>, Iterable<Hotel> {
    private JsonDataParser parser;
    private SortedMap<String, Hotel> hotelMap;

    public HotelData(JsonDataParser parser) {
        this.parser = parser;
        hotelMap = new TreeMap<>();
    }

    /**
     * Loads json file using factory method, and add Hotel object to the hashMap.
     * @param path to be loaded
     */
    @Override
    public void loadJson(Path path) {
        List<Hotel> hotels = parser.parse(path);
        for (Hotel hotel : hotels) {
            add(hotel);
        }
    }

    /**
     * Adds Hotel object to the hashMap if there doesn't exist the hotelId.
     * @param hotel to be added to the hashMap.
     */
    @Override
    public void add(Hotel hotel) {
        String hotelId = hotel.getHotelId();
        if (!hotelMap.containsKey(hotelId))
            hotelMap.put(hotelId, hotel);
    }

    /**
     * Finds Hotel object by hotelId. This is safe to user since it returns String.
     * @param hotelId to be searched.
     * @return String of the Hotel object if exists
     */
    @Override
    public String find(String hotelId) {
        if (hotelMap.containsKey(hotelId)) {
            String res = hotelMap.get(hotelId).toString();
            return res;
        }
        return "";
    }

    /**
     * Finds Hotel object by hotelId and return its hotelName in String.
     * @param hotelId to be found a Hotel object.
     * @return hotelName of the Hotel object found by the hotelId.
     */
    public String findHotelName(String hotelId) {
        if (hotelMap.containsKey(hotelId)) {
            String res = hotelMap.get(hotelId).getHotelName();
            return res;
        }
        return "";
    }

    /**
     * Finds Hotel object by hotelId and return it in JsonObject.
     * @param hotelId to be found a Hotel object.
     * @param num should be ignored.
     * @return Optional of JsonObject of the hotel object found by the hotelId.
     */
    @Override
    public Optional<JsonObject> findInJson(String hotelId, int num) {
        if (hotelMap.containsKey(hotelId)) {
            return Optional.of(hotelMap.get(hotelId).serialize());
        }
        return Optional.empty();
    }

    /**
     * This method is for iterating Hotel objects in the hashMap.
     * @return iterator of the Hotel object.
     */
    @Override
    public Iterator<Hotel> iterator() {
        return hotelMap.values().iterator();
    }

    public Optional<Hotel> findHotelByValue(String hotelId) {
        try {
            if (hotelMap.containsKey(hotelId)) {
                System.out.println("Returning..");
                Hotel cloned = hotelMap.get(hotelId).clone();
                return Optional.of(cloned);
            }
            return Optional.empty();
        } catch (CloneNotSupportedException e) {
            System.out.println("error");
            return Optional.empty();
        }
    }

    public List<Hotel> findHotelsByKeyword(String keyword) {
        List<Hotel> hotelList = new ArrayList<>();
        try {
            for (Hotel hotel : hotelMap.values()) {
                if (hotel.getHotelName().contains(keyword)) {
                    Hotel cloned = hotel.clone();
                    hotelList.add(cloned);
                }
            }
            return hotelList;
        } catch (CloneNotSupportedException e) {
            return hotelList;
        }
    }
}
