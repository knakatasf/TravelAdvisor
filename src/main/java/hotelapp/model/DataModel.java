package hotelapp.model;

import com.google.gson.JsonObject;

import java.nio.file.Path;
import java.util.Optional;

public interface DataModel<T> {
    void loadJson(Path path);
    void add(T data);
    String find(String hotelId);
    Optional<JsonObject> findInJson(String hotelId, int num);
}

