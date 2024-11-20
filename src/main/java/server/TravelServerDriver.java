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
        Properties config = new Properties();
        try (FileReader fr = new FileReader("database.properties")) {
            config.load(fr);

            String uri = "jdbc:mysql://" + config.getProperty("hostname") + "/" + config.getProperty("database") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
            System.out.println("connected!");
        } catch (IOException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
