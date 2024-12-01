package hotelapp.model.entity;

import com.google.gson.JsonObject;

/**
 * Entity class for a Hotel object.
 * Utilizes builder pattern to ensure all the data member is valid.
 */
public class Hotel implements Comparable<Hotel>, Cloneable {
    private String hotelId;
    private String hotelName;
    private String addr;
    private String city;
    private String state;
    private String lat;
    private String lng;

    public Hotel(HotelBuilder builder) {
        this.hotelName = builder.hotelName;
        this.hotelId = builder.hotelId;
        this.addr = builder.addr;
        this.city = builder.city;
        this.state = builder.state;
        this.lat = builder.lat;
        this.lng = builder.lng;
    }

    public static class HotelBuilder {
        private String hotelName;
        private String hotelId;
        private String addr;
        private String city;
        private String state;
        private String lat;
        private String lng;

        public HotelBuilder hotelId(String hotelId) {
            this.hotelId = hotelId;
            return this;
        }

        public HotelBuilder hotelName(String hotelName) {
            this.hotelName = hotelName;
            return this;
        }

        public HotelBuilder addr(String addr) {
            this.addr = addr;
            return this;
        }

        public HotelBuilder city(String city) {
            this.city = city;
            return this;
        }

        public HotelBuilder state(String state) {
            this.state = state;
            return this;
        }

        public HotelBuilder lat(String lat) {
            this.lat = lat;
            return this;
        }

        public HotelBuilder lng(String lng) {
            this.lng = lng;
            return this;
        }

        /**
         * Builds a Hotel object using builder pattern; Ensures all the data members are valid.
         * @return Hotel object
         * @throws IllegalArgumentException if some of the data member is null
         */
        public Hotel build() throws IllegalArgumentException {
            if (isValid())
                return new Hotel(this);
            throw new IllegalArgumentException("Some data member is invalid.");
        }

        /**
         * Checks if any data member is invalid.
         * @return true all the data member is valid, false otherwise.
         */
        private boolean isValid() {
            return this.hotelName != null && this.hotelId != null
                    && this.addr != null && this.city != null
                    && this.state != null && this.lat != null && this.lng != null;
        }
    }

    public String getHotelName() { return hotelName; }
    public String getHotelId() { return hotelId; }
    public String getAddr() { return addr; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getLat() { return lat; }
    public String getLng() { return lng; }
    public String getAddress() { return addr + ", " + city + ", " + state; }
    public String getExpediaLink() {
        String hotelNameForLink = hotelName.replace("/", " ").replace(" ", "-");
        return String.format("https://www.expedia.com/%s-Hotels-%s.h%s.Hotel-Information",
                getCity(), hotelNameForLink, getHotelId());
    }

    /**
     * Serializes a Hotel object to Json Object.
     * @return jsonObject of the Hotel object.
     */
    public JsonObject serialize() {
        JsonObject hotelJo = new JsonObject();
        hotelJo.addProperty("hotelId", getHotelId());
        hotelJo.addProperty("name", getHotelName());
        hotelJo.addProperty("addr", getAddr());
        hotelJo.addProperty("city", getCity());
        hotelJo.addProperty("state", getState());
        hotelJo.addProperty("lat", getLat());
        hotelJo.addProperty("lng", getLng());
        return hotelJo;
    }

    @Override
    public int compareTo(Hotel other) {
        return this.getHotelId().compareTo(other.getHotelId());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getHotelName() + ": " + getHotelId() + System.lineSeparator());
        sb.append(getAddr() + System.lineSeparator());
        sb.append(getCity() + System.lineSeparator());
        sb.append(getState() + System.lineSeparator());
        sb.append(getLat() + " - " + getLng());
        return sb.toString();
    }

    /**
     * Returns a deep copy of the hotel object.
     * @return Deep copy of the hotel object.
     * @throws CloneNotSupportedException
     */
    @Override
    public Hotel clone() throws CloneNotSupportedException {
        return (Hotel)super.clone();
    }
}
