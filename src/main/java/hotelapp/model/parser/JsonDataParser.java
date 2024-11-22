package hotelapp.model.parser;

import java.nio.file.Path;
import java.util.List;

public interface JsonDataParser<T> {
    public List<T> parse(Path p);
}
