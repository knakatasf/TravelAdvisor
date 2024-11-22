package hotelapp.model.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hotelapp.model.entity.Hotel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * JsonParser to parse Hotel object from a json file; Implements factory method.
 * This class can be injected into model class so that model class can store Hotel objects.
 */
public class HotelJsonParser implements JsonDataParser<Hotel> {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parses Hotel objects from a json file.
     * @param p to be parsed Hotel objects.
     * @return ArrayList<Hotel>
     */
    @Override
    public List<Hotel> parse(Path p) {
        List<Hotel> hotels = new ArrayList<>();
        try {
            FileReader fr = new FileReader(p.toString());
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(fr);
            JsonArray jsonArray = jo.getAsJsonArray("sr");

            for (JsonElement element : jsonArray) {
                JsonObject jsonObj = element.getAsJsonObject();

                String hotelId = jsonObj.get("id").getAsString();
                String hotelName = jsonObj.get("f").getAsString();
                String addr = jsonObj.get("ad").getAsString();
                String city = jsonObj.get("ci").getAsString();
                String state = jsonObj.get("pr").getAsString();
                String lat = ((JsonObject)(jsonObj.get("ll"))).get("lat").getAsString();
                String lng = ((JsonObject)(jsonObj.get("ll"))).get("lng").getAsString();

                Hotel.HotelBuilder builder = new Hotel.HotelBuilder();
                hotels.add(builder.hotelName(hotelName).hotelId(hotelId)
                        .addr(addr).city(city).state(state).lat(lat).lng(lng)
                        .build());
            }
        } catch (FileNotFoundException e) {
            logger.error("File couldn't be found: " + p);
            System.out.println("File couldn't be found: " + e);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid data member in the json file found: " + p);
            System.out.println(e.getMessage());
        }
        return hotels;
    }
}
