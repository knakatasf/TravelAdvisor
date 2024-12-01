package database;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Properties;
import java.util.Random;

public class TravelDatabaseManager {
    private static TravelDatabaseManager dbModel = new TravelDatabaseManager("database.properties");
    private Properties config;
    private String uri = null;
    private Random random = new Random();


    private TravelDatabaseManager(String propertiesFile) {
        this.config = loadConfigFile(propertiesFile);
        this.uri = "jdbc:mysql://"+ config.getProperty("hostname") + "/" + config.getProperty("database") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    }

    /**
     * Singleton pattern
     * @return static object of databaseManager.
     */
    public static TravelDatabaseManager getInstance() { return dbModel; }

    /**
     * Loads configuration contained in the propertyFile.
     * @param propertyFile contains database username and password
     * @return Properties that contains username and password for the database.
     */
    private Properties loadConfigFile(String propertyFile) {
        Properties config = new Properties();
        try (FileReader fr = new FileReader(propertyFile)) {
            config.load(fr);
        } catch (IOException e) {
            System.out.println("The file was not found: " + propertyFile);
            System.out.println(e.getMessage());
        }
        return config;
    }

    /**
     * Creates tables for the database.
     */
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

    /**
     * Create usersalt which is unique for the user.
     * @param bytes random number
     * @param length length
     * @return usersalt for the user
     */
    public static String encodeHex(byte[] bytes, int length) {
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);

        assert hex.length() == length;
        return hex;
    }

    /**
     * Using usersalt which is unique for the user, creates hashed password for the user
     * @param password to be hashed
     * @param salt to be used to hash the password
     * @return hashed password
     */
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

    /**
     * Checks if the username is already taken or not.
     * @param username to be checked
     * @return true if the username is already take, false otherwise.
     * @throws SQLException
     */
    public boolean isDuplicatedUsername(String username) throws SQLException {
        PreparedStatement statement = null;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = connection.prepareStatement(PreparedStatements.CHECK_DUPLICATES_SQL);
            statement.setString(1, username);

            ResultSet results = statement.executeQuery();
            if (results.next())
                return true;
            return false;
        } catch (SQLException e) {
            throw new SQLException("Database connection failed.");
        }
    }

    /**
     * Creates usersalt which is unique to the user, and hashes the password using usersalt. Insert all of them to database.
     * @param username of the user
     * @param password to be hashed using usersalt
     * @throws SQLException
     */
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

    /**
     * Takes username and get usersalt from the database. Hashes the password the user inputs and checks if they are identical.
     * @param username to get usersalt and hashed password in the database.
     * @param password to be hashed using usersalt
     * @return true if valid, false otherwise.
     */
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

    /**
     * Takes username and gets usersalt in the database.
     * @param connection to be connected to the database
     * @param user username
     * @return usersalt in the database.
     */
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
