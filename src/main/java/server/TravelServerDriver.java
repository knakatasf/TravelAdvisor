package server;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class TravelServerDriver {
    public static final int PORT = 8080;

    public static void main(String[] args) {
        // FILL IN CODE, and add more classes as needed
        TravelServer server = new TravelServer();
        server.start();
    }
}
