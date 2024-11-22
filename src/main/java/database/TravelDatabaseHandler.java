package database;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Properties;
import java.util.Random;

public class TravelDatabaseHandler {
    private static TravelDatabaseHandler dbModel = new TravelDatabaseHandler("database.properties");
    private Properties config;
    private String uri = null;
    private Random random = new Random();


    private TravelDatabaseHandler(String propertiesFile) {
        this.config = loadConfigFile(propertiesFile);
        this.uri = "jdbc:mysql://"+ config.getProperty("hostname") + "/" + config.getProperty("database") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    }

    public static TravelDatabaseHandler getInstance() { return dbModel; }

    public Properties loadConfigFile(String propertyFile) {
        Properties config = new Properties();
        try (FileReader fr = new FileReader(propertyFile)) {
            config.load(fr);
        } catch (IOException e) {
            System.out.println("The file was not found: " + propertyFile);
            System.out.println(e.getMessage());
        }
        return config;
    }

    public void createTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            statement.execute(PreparedStatements.CREATE_USER_TABLE_SQL);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static String encodeHex(byte[] bytes, int length) {
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);

        assert hex.length() == length;
        return hex;
    }

    public static String getHash(String password, String salt) {
        String salted = salt + password;
        String hashed = salted;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salted.getBytes());
            hashed = encodeHex(md.digest(), 64);
        } catch (Exception e) {
            System.out.println("Getting hash failed.");
        }
        return hashed;
    }

    public void registerUser(String username, String password) throws SQLException {
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);

        String usersalt = encodeHex(saltBytes, 32);
        String passhash = getHash(password, usersalt);

        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = connection.prepareStatement(PreparedStatements.REGISTER_USER_SQL);
            statement.setString(1, username);
            statement.setString(2, passhash);
            statement.setString(3, usersalt);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            throw new SQLException("Inserting the user failed: " + username);
        }
    }

    public boolean authenticateUser(String username, String password) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.AUTH_SQL);
            String usersalt = getSalt(connection, username);
            String passhash = getHash(password, usersalt);

            statement.setString(1, username);
            statement.setString(2, passhash);
            ResultSet results = statement.executeQuery();
            return results.next();
        } catch (SQLException e) {
            System.out.println("Database connection failed.");
        }
        return false;
    }

    private String getSalt(Connection connection, String user) {
        String salt = null;
        try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.SALT_SQL)) {
            statement.setString(1, user);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                salt = results.getString("usersalt");
                return salt;
            }
        } catch (SQLException e) {
            System.out.println("Getting salt failed.");
        }
        return salt;
    }
}
